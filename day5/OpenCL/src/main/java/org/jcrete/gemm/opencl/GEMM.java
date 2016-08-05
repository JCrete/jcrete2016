package org.jcrete.gemm.opencl;

import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opencl.CLProgramCallback;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

import static java.lang.Math.*;
import static org.lwjgl.opencl.CL10.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

class GEMM {

	private static final String KERNEL = System.getProperty("KERNEL", "gemm_nn");

	private static final int SIZE = Integer.parseInt(System.getProperty("SIZE", "3968"));
	private static final int ITERATIONS = Integer.parseInt(System.getProperty("ITERATIONS", "10"));

	private static final boolean VALIDATION = Boolean.parseBoolean(System.getProperty("VALIDATION", "false"));

	private static final String T = System.getProperty("T", "float");
	private static final int SIZEOF_T = "float".equals(T) ? 4 : 8;

	private static final int TILE_SIZE_M = Integer.parseInt(System.getProperty("TILE_SIZE_M", "1"));
	private static final int TILE_GROUP_M = Integer.parseInt(System.getProperty("TILE_GROUP_M", "16"));
	private static final int TILE_SIZE_N = Integer.parseInt(System.getProperty("TILE_SIZE_N", "128"));
	private static final int TILE_GROUP_N = Integer.parseInt(System.getProperty("TILE_GROUP_N", "1"));
	private static final int TILE_SIZE_K = Integer.parseInt(System.getProperty("TILE_SIZE_K", "8"));

	private final ByteBuffer source;

	private final long program;
	private final long kernel;

	GEMM(long device, long context) {
		try {
			source = IOUtil.ioResourceToByteBuffer("gemm.cl", 8192);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		try (MemoryStack stack = stackPush()) {
			IntBuffer pErrcode = stack.callocInt(1);

			PointerBuffer strings = stack.callocPointer(1);
			PointerBuffer lengths = stack.callocPointer(1);

			strings.put(0, source);
			lengths.put(0, source.remaining());

			program = clCreateProgramWithSource(context, strings, lengths, pErrcode);
			InfoUtil.checkCLError(pErrcode);

			CountDownLatch latch = new CountDownLatch(1);

			StringBuilder options = new StringBuilder(128)
				.append("-DT=").append(T).append(' ')
				.append("-DTILE_SIZE_M=").append(TILE_SIZE_M).append(' ')
				.append("-DTILE_GROUP_M=").append(TILE_GROUP_M).append(' ')
				.append("-DTILE_SIZE_N=").append(TILE_SIZE_N).append(' ')
				.append("-DTILE_GROUP_N=").append(TILE_GROUP_N).append(' ')
				.append("-DTILE_SIZE_K=").append(TILE_SIZE_K);

			System.out.println("OpenCL COMPILER OPTIONS: " + options);

			CLProgramCallback buildCallback = null;
			try {
				int errcode = clBuildProgram(program, device, options, buildCallback = CLProgramCallback.create((program, user_data) -> {
					System.out.format(
						"The cl_program [0x%X] was built %s\n",
						program,
						InfoUtil.getProgramBuildInfoInt(program, device, CL_PROGRAM_BUILD_STATUS) == CL_SUCCESS ? "successfully" : "unsuccessfully"
					);
					String log = InfoUtil.getProgramBuildInfoStringASCII(program, device, CL_PROGRAM_BUILD_LOG);
					if ( !log.isEmpty() )
						System.out.format("BUILD LOG:\n----\n%s\n-----\n", log);

					latch.countDown();
				}), NULL);
				InfoUtil.checkCLError(errcode);

				// Make sure the program has been built before proceeding
				try {
					latch.await();
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			} finally {
				if ( buildCallback != null )
					buildCallback.free();
			}

			// init kernel with constants
			kernel = clCreateKernel(program, KERNEL, pErrcode);
			InfoUtil.checkCLError(pErrcode);
		}
	}

	void destroy() {
		clReleaseKernel(kernel);
		clReleaseProgram(program);
	}

	void exec(long device, long context, long queue) {
		int rowAlignment = requiredOpenCLAlignment(device);

		System.out.format("Running %s kernel with matrix size: %d x %d\n", KERNEL, SIZE, SIZE);

		// Ensures that each matrix memory row is aligned
		int stride = (SIZE * SIZEOF_T + rowAlignment - 1) & ~(rowAlignment - 1);
		System.out.format("Memory row stride to ensure necessary alignment: %d bytes\n", stride);
		// calculate row stride in elements of T
		stride /= SIZEOF_T;
		assert SIZE <= stride;

		int matrix_memory_size = SIZE * stride * SIZEOF_T;
		System.out.format("Size of memory region for one matrix: %d bytes\n", matrix_memory_size);

		// Allocate aligned memory for matrices to use them in
		// buffers with CL_MEM_USE_HOST_PTR.

		int alignmentForPtr = zeroCopyPtrAlignment(device);
		int alignedSize = zeroCopySizeAlignment(matrix_memory_size, device);

		MatrixUtil util = SIZEOF_T == 4
			? new MatrixUtilFloat()
			: new MatrixUtilDouble();

		ByteBuffer
			hostA = memAlignedAlloc(alignmentForPtr, alignedSize),
			hostB = memAlignedAlloc(alignmentForPtr, alignedSize),
			hostC = memAlignedAlloc(alignmentForPtr, alignedSize);

		util.fill(hostA, hostB, hostC);

		// -----------------------------------------------------------------------
		// Allocating device-side resources for matrices
		// -----------------------------------------------------------------------

		// Create OpenCL buffers for the matrices based on allocated memory regions
		// Create buffers with CL_MEM_USE_HOST_PTR to minimize copying and
		// model situation when matrices are hosted by some native library that
		// uses OpenCL to accelerate calculations.

		long
			deviceA,
			deviceB,
			deviceC;

		try (MemoryStack stack = stackPush()) {
			IntBuffer pErrcode = stack.callocInt(1);

			deviceA = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_USE_HOST_PTR, hostA, pErrcode);
			InfoUtil.checkCLError(pErrcode);
			deviceB = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_USE_HOST_PTR, hostB, pErrcode);
			InfoUtil.checkCLError(pErrcode);
			deviceC = clCreateBuffer(context, CL_MEM_READ_WRITE | CL_MEM_USE_HOST_PTR, hostC, pErrcode);
			InfoUtil.checkCLError(pErrcode);
		}

		// -----------------------------------------------------------------------
		// Setting kernel arguments
		// -----------------------------------------------------------------------

		InfoUtil.checkCLError(clSetKernelArg1p(kernel, 0, deviceA));
		InfoUtil.checkCLError(clSetKernelArg1i(kernel, 1, stride));

		InfoUtil.checkCLError(clSetKernelArg1p(kernel, 2, deviceB));
		InfoUtil.checkCLError(clSetKernelArg1i(kernel, 3, stride));

		InfoUtil.checkCLError(clSetKernelArg1p(kernel, 4, deviceC));
		InfoUtil.checkCLError(clSetKernelArg1i(kernel, 5, stride));

		InfoUtil.checkCLError(clSetKernelArg1i(kernel, 6, SIZE));

		util.setABKernelArgs(kernel, 7, 8);

		// -----------------------------------------------------------------------
		// Define ndrange iteration space: global and local sizes based on
		// parameters obtained from user.

		// Refer to the sample documentation for clarification about
		// how work is devided among work-groups and work-items.
		// -----------------------------------------------------------------------

		try (MemoryStack stack = stackPush()) {
			PointerBuffer global_size = stack.pointers(
				SIZE / TILE_SIZE_M,
				SIZE / TILE_SIZE_N
			);

			PointerBuffer local_size = stack.pointers(
				TILE_GROUP_M,
				TILE_GROUP_N
			);

			// theoretical number of floating point operations (addition and multiplication) for one kernel execution
			// needed for performance calculations (GFLOPS) at every iteration below
			double flops = (double)SIZE * SIZE * (
				SIZE + // multiplications
					SIZE + // additions
					2      // multiplication by alpha and beta
			);

			// -----------------------------------------------------------------------
			// Loop with the kernel invocation
			// -----------------------------------------------------------------------

			for ( int i = 0; i < ITERATIONS; ++i ) {
				// Here we start measuring host time for kernel execution
				long start = System.nanoTime();

				InfoUtil.checkCLError(clEnqueueNDRangeKernel(
					queue,
					kernel,
					2,
					null,
					global_size,
					local_size,
					null, null
				));

				InfoUtil.checkCLError(clFinish(queue));

				// It is important to measure end host time after clFinish call
				long end = System.nanoTime();

				double time = (end - start) / 1000L / 1000L / 1000.0;

				System.out.format("Host time: %f sec.\n", time);
				System.out.format("Host perf: %f GFLOPS\n", flops / time / 1e9);

				if ( i == 0 && VALIDATION ) {
					// Validate result for the first iteration only and
					// only if user wants this.
					// Please note, validation procedure cannot be run at
					// futher iterations after the very first iteration,
					// as the results are being accumulated in C matrix
					// every iteration but validation procedures assumes that
					// C initial values are all zeros.

					IntBuffer pErrcode = stack.callocInt(1);

					ByteBuffer mappedC = clEnqueueMapBuffer(
						queue,
						deviceC,
						CL_TRUE,    // blocking map
						CL_MAP_READ,
						0L,
						matrix_memory_size,
						null, null,
						pErrcode,
						null
					);
					InfoUtil.checkCLError(pErrcode);

					// After map call, host-memory area for matrix C is
					// automatically updated with the latest bits from the device
					// So we just use it by original pointer as well as input matrices:
					if (
						!util.checkValidity(
							hostA,
							hostB,
							mappedC,
							SIZE,
							stride,
							"gemm_nt".equals(KERNEL)    // whether B is transposed or not
						)
						) {
						throw new IllegalStateException("Validation procedure reported failures");
					}

					InfoUtil.checkCLError(clEnqueueUnmapMemObject(
						queue,
						deviceC,
						mappedC,
						null, null
					));

					// Finish here is only required for correct time measurement on the next iteration
					// It does not affect correctness of calculations because you use the in-order OpenCL queue here.
					InfoUtil.checkCLError(clFinish(queue));
				}
			}
		}

		clReleaseMemObject(deviceC);
		clReleaseMemObject(deviceB);
		clReleaseMemObject(deviceA);

		memAlignedFree(hostC);
		memAlignedFree(hostB);
		memAlignedFree(hostA);
	}

	/**
	 * Minimal alignment in bytes for memory used in clCreateBuffer with CL_MEM_USE_HOST_PTR.
	 * <p>
	 * This is the minimal value required by OpenCL spec, but it may be insufficient for the best performance on Intel
	 * Processor Graphics.
	 *
	 * @return the minimal alignment in bytes
	 */
	private static int requiredOpenCLAlignment(long device) {
		try (MemoryStack stack = stackPush()) {
			IntBuffer result = stack.callocInt(1);

			int errcode = clGetDeviceInfo(device, CL_DEVICE_MEM_BASE_ADDR_ALIGN, result, null);
			InfoUtil.checkCLError(errcode);

			assert result.get(0) % 8 == 0;
			return result.get(0) / 8; // clGetDeviceInfo returns value in bits, convert it to bytes
		}
	}

	private static int zeroCopyPtrAlignment(long device) {
		// Please refer to Intel Zero Copy Tutorial and OpenCL Performance Guide
		return 4096;
	}

	private static int zeroCopySizeAlignment(int requiredSize, long device) {
		// Please refer to Intel Zero Copy Tutorial and OpenCL Performance Guide
		// The following statement rounds requiredSize up to the next 64-byte boundary
		return requiredSize + (~requiredSize + 1) % 64;   // or even shorter: requiredSize + (-requiredSize) % 64
	}

	private interface MatrixUtil {

		void fill(ByteBuffer matrixA, ByteBuffer matrixB, ByteBuffer matrixC);

		void setABKernelArgs(long kernel, int indexA, int indexB);

		boolean checkValidity(ByteBuffer matrixA, ByteBuffer matrixB, ByteBuffer matrixC, int size, int stride, boolean transposed);

	}

	private static class MatrixUtilFloat implements MatrixUtil {

		private static final float FLT_EPSILON = 1.192092896e-07F;

		private final float alpha;
		private final float beta;

		MatrixUtilFloat() {
			ThreadLocalRandom rng = ThreadLocalRandom.current();

			alpha = rng.nextFloat();
			beta = rng.nextFloat();

			System.out.format("Using alpha = %f and beta = %f\n", alpha, beta);
		}

		@Override
		public void fill(ByteBuffer matrixA, ByteBuffer matrixB, ByteBuffer matrixC) {
			ThreadLocalRandom rng = ThreadLocalRandom.current();

			// Fill the rows with random values from range [0, 1)
			fill(matrixA.asFloatBuffer(), rng);
			fill(matrixB.asFloatBuffer(), rng);

			// To simplify validation a bit, we initialize C matrix with all zeros.
			// It should not affect performance, which should be identical to
			// the general case.
			BufferUtils.zeroBuffer(matrixC);
		}

		private static void fill(FloatBuffer matrix, ThreadLocalRandom rng) {
			for ( int i = 0; i < matrix.capacity(); i++ )
				matrix.put(i, rng.nextFloat());
		}

		@Override
		public void setABKernelArgs(long kernel, int indexA, int indexB) {
			InfoUtil.checkCLError(clSetKernelArg1f(kernel, indexA, alpha));
			InfoUtil.checkCLError(clSetKernelArg1f(kernel, indexB, beta));
		}

		@Override
		public boolean checkValidity(ByteBuffer matrixA, ByteBuffer matrixB, ByteBuffer matrixC, int size, int stride, boolean transposed) {
			System.out.println("Validate output...");

			// Btransposed == false, lstride = 1
			int lstride = transposed ? stride : 1;
			int jstride = transposed ? 1 : stride;

			// Estimate error tolerance for a given type T and relying on the fact
			// that initial matrix values are from [0, 1]
			float max_value = 1.0f;
			float error_tol = 2.0f * alpha * max_value * max_value * 2.0f * size * FLT_EPSILON;

			FloatBuffer A = matrixA.asFloatBuffer();
			FloatBuffer B = matrixB.asFloatBuffer();
			FloatBuffer C = matrixC.asFloatBuffer();
			for ( int i = 0; i < size; ++i ) {
				for ( int j = 0; j < size; ++j ) {
					// compute golden value for c[i][j] element
					float accum = 0.0f;
					for ( int l = 0; l < size; ++l ) {
						accum += A.get(l * stride + i) * B.get(l * lstride + j * jstride);
					}

					float golden = alpha * accum;

					float absdiff = abs(C.get(j * stride + i) - golden);
					if ( absdiff > error_tol ) {
						System.out.println(" FAILED");
						System.out.print(
							"\nVALIDATION FAILED!!!\n    reference" + "[" + i + ", " + j + "] = "
								+ golden + ",\n    calculated" + "[" + i + ", " + j + "] = "
								+ C.get(j * stride + i)
								+ ",\n    absolute difference" + "[" + i + ", " + j + "] = " + absdiff + "\n"
								+ "Further validation was stopped\n\n"
						);
						return false;
					}
				}
			}

			System.out.println(" PASSED");
			return true;
		}
	}

	private static class MatrixUtilDouble implements MatrixUtil {

		private static final double DBL_EPSILON = 2.2204460492503131e-016;

		private final double alpha;
		private final double beta;

		MatrixUtilDouble() {
			ThreadLocalRandom rng = ThreadLocalRandom.current();

			alpha = rng.nextDouble();
			beta = rng.nextDouble();

			System.out.format("Using alpha = %f and beta = %f\n", alpha, beta);
		}

		@Override
		public void fill(ByteBuffer matrixA, ByteBuffer matrixB, ByteBuffer matrixC) {
			ThreadLocalRandom rng = ThreadLocalRandom.current();

			// Fill the rows with random values from range [0, 1)
			fill(matrixA.asDoubleBuffer(), rng);
			fill(matrixB.asDoubleBuffer(), rng);

			// To simplify validation a bit, we initialize C matrix with all zeros.
			// It should not affect performance, which should be identical to
			// the general case.
			BufferUtils.zeroBuffer(matrixC);
		}

		private static void fill(DoubleBuffer matrix, ThreadLocalRandom rng) {
			for ( int i = 0; i < matrix.capacity(); i++ )
				matrix.put(i, rng.nextDouble());
		}

		@Override
		public void setABKernelArgs(long kernel, int indexA, int indexB) {
			InfoUtil.checkCLError(clSetKernelArg1d(kernel, indexA, alpha));
			InfoUtil.checkCLError(clSetKernelArg1d(kernel, indexB, beta));
		}

		@Override
		public boolean checkValidity(ByteBuffer matrixA, ByteBuffer matrixB, ByteBuffer matrixC, int size, int stride, boolean transposed) {
			System.out.println("Validate output...");

			// Btransposed == false, lstride = 1
			int lstride = transposed ? stride : 1;
			int jstride = transposed ? 1 : stride;

			// Estimate error tolerance for a given type T and relying on the fact
			// that initial matrix values are from [0, 1]
			double max_value = 1.0;
			double error_tol = 2.0 * alpha * max_value * max_value * 2.0 * size * DBL_EPSILON;

			DoubleBuffer A = matrixA.asDoubleBuffer();
			DoubleBuffer B = matrixB.asDoubleBuffer();
			DoubleBuffer C = matrixC.asDoubleBuffer();
			for ( int i = 0; i < size; ++i ) {
				for ( int j = 0; j < size; ++j ) {
					// compute golden value for c[i][j] element
					double accum = 0.0f;
					for ( int l = 0; l < size; ++l ) {
						accum += A.get(l * stride + i) * B.get(l * lstride + j * jstride);
					}

					double golden = alpha * accum;

					double absdiff = abs(C.get(j * stride + i) - golden);
					if ( absdiff > error_tol ) {
						System.out.println(" FAILED");
						System.out.print(
							"\nVALIDATION FAILED!!!\n    reference" + "[" + i + ", " + j + "] = "
								+ golden + ",\n    calculated" + "[" + i + ", " + j + "] = "
								+ C.get(j * stride + i)
								+ ",\n    absolute difference" + "[" + i + ", " + j + "] = " + absdiff + "\n"
								+ "Further validation was stopped\n\n"
						);
						return false;
					}
				}
			}

			System.out.println(" PASSED");
			return true;
		}

	}

}