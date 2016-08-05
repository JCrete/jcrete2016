package org.jcrete.gemm.java;

import java.util.concurrent.ThreadLocalRandom;

import static java.lang.Math.*;

class GEMM {

	private static final String KERNEL = System.getProperty("KERNEL", "gemm_nn");

	static final int SIZE = Integer.parseInt(System.getProperty("SIZE", "3968"));
	private static final int ITERATIONS = Integer.parseInt(System.getProperty("ITERATIONS", "10"));

	private static final boolean VALIDATION = Boolean.parseBoolean(System.getProperty("VALIDATION", "false"));

	private static final String T = System.getProperty("T", "float");

	GEMM() {
	}

	void exec() {
		System.out.format("Running %s %s kernel with matrix size: %d x %d\n", T, KERNEL, SIZE, SIZE);

		int stride = SIZE;
		int matrix_size = SIZE * stride;

		MatrixUtil util = "float".equals(T)
			? new MatrixUtilFloat(matrix_size)
			: new MatrixUtilDouble(matrix_size);

		// -----------------------------------------------------------------------
		// Define ndrange iteration space: global and local sizes based on
		// parameters obtained from user.

		// Refer to the sample documentation for clarification about
		// how work is devided among work-groups and work-items.
		// -----------------------------------------------------------------------

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

			util.exec(stride);

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

				// After map call, host-memory area for matrix C is
				// automatically updated with the latest bits from the device
				// So we just use it by original pointer as well as input matrices:
				if (
					!util.checkValidity(

						SIZE,
						stride,
						"gemm_nt".equals(KERNEL)    // whether B is transposed or not
					)
					) {
					throw new IllegalStateException("Validation procedure reported failures");
				}
			}
		}
	}

	private interface MatrixUtil {

		void exec(int stride);

		boolean checkValidity(int size, int stride, boolean transposed);

	}

	private static class MatrixUtilFloat implements MatrixUtil {

		private static final float FLT_EPSILON = 1.192092896e-07F;

		private final float[] matrixA;
		private final float[] matrixB;
		private final float[] matrixC;

		private final float alpha;
		private final float beta;

		MatrixUtilFloat(int matrix_size) {
			ThreadLocalRandom rng = ThreadLocalRandom.current();

			matrixA = new float[matrix_size];
			matrixB = new float[matrix_size];
			matrixC = new float[matrix_size];

			// Fill the rows with random values from range [0, 1)
			fill(matrixA, rng);
			fill(matrixB, rng);

			alpha = rng.nextFloat();
			beta = rng.nextFloat();

			System.out.format("Using alpha = %f and beta = %f\n", alpha, beta);
		}

		private static void fill(float[] matrix, ThreadLocalRandom rng) {
			for ( int i = 0; i < matrix.length; i++ )
				matrix[i] = rng.nextFloat();
		}

		@Override
		public void exec(int stride) {
			for ( int i = 0; i < SIZE; i++ ) {
				for ( int j = 0; j < SIZE; j++ ) {
					float c = 0.0f;
					for ( int k = 0; k < SIZE; k++ ) {
						c += matrixA[k * stride + i] * matrixB[j * stride + k];
					}
					matrixC[j * stride + i] = alpha * c + beta * matrixC[j * stride + i];
				}
			}
		}

		public boolean checkValidity(int size, int stride, boolean transposed) {
			System.out.println("Validate output...");

			// Btransposed == false, lstride = 1
			int lstride = transposed ? stride : 1;
			int jstride = transposed ? 1 : stride;

			// Estimate error tolerance for a given type T and relying on the fact
			// that initial matrix values are from [0, 1]
			float max_value = 1.0f;
			float error_tol = 2.0f * alpha * max_value * max_value * 2.0f * size * FLT_EPSILON;

			float[] A = matrixA;
			float[] B = matrixB;
			float[] C = matrixC;
			for ( int i = 0; i < size; ++i ) {
				for ( int j = 0; j < size; ++j ) {
					// compute golden value for c[i][j] element
					float accum = 0.0f;
					for ( int l = 0; l < size; ++l ) {
						accum += A[l * stride + i] * B[l * lstride + j * jstride];
					}

					float golden = alpha * accum;

					float absdiff = abs(C[j * stride + i] - golden);
					if ( absdiff > error_tol ) {
						System.out.println(" FAILED");
						System.out.print(
							"\nVALIDATION FAILED!!!\n    reference" + "[" + i + ", " + j + "] = "
								+ golden + ",\n    calculated" + "[" + i + ", " + j + "] = "
								+ C[j * stride + i]
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

		private final double[] matrixA;
		private final double[] matrixB;
		private final double[] matrixC;

		private final double alpha;
		private final double beta;

		MatrixUtilDouble(int matrix_size) {
			ThreadLocalRandom rng = ThreadLocalRandom.current();

			matrixA = new double[matrix_size];
			matrixB = new double[matrix_size];
			matrixC = new double[matrix_size];

			// Fill the rows with random values from range [0, 1)
			fill(matrixA, rng);
			fill(matrixB, rng);

			alpha = rng.nextDouble();
			beta = rng.nextDouble();

			System.out.format("Using alpha = %f and beta = %f\n", alpha, beta);
		}

		private static void fill(double[] matrix, ThreadLocalRandom rng) {
			for ( int i = 0; i < matrix.length; i++ )
				matrix[i] = rng.nextDouble();
		}

		@Override
		public void exec(int stride) {
			for ( int i = 0; i < SIZE; i++ ) {
				for ( int j = 0; j < SIZE; j++ ) {
					double c = 0.0f;
					for ( int k = 0; k < SIZE; k++ ) {
						c += matrixA[k * stride + i] * matrixB[j * stride + k];
					}
					matrixC[j * stride + i] = alpha * c + beta * matrixC[j * stride + i];
				}
			}
		}

		public boolean checkValidity(int size, int stride, boolean transposed) {
			System.out.println("Validate output...");

			// Btransposed == false, lstride = 1
			int lstride = transposed ? stride : 1;
			int jstride = transposed ? 1 : stride;

			// Estimate error tolerance for a given type T and relying on the fact
			// that initial matrix values are from [0, 1]
			double max_value = 1.0;
			double error_tol = 2.0 * alpha * max_value * max_value * 2.0 * size * DBL_EPSILON;

			double[] A = matrixA;
			double[] B = matrixB;
			double[] C = matrixC;
			for ( int i = 0; i < size; ++i ) {
				for ( int j = 0; j < size; ++j ) {
					// compute golden value for c[i][j] element
					double accum = 0.0;
					for ( int l = 0; l < size; ++l ) {
						accum += A[l * stride + i] * B[l * lstride + j * jstride];
					}

					double golden = alpha * accum;

					double absdiff = abs(C[j * stride + i] - golden);
					if ( absdiff > error_tol ) {
						System.out.println(" FAILED");
						System.out.print(
							"\nVALIDATION FAILED!!!\n    reference" + "[" + i + ", " + j + "] = "
								+ golden + ",\n    calculated" + "[" + i + ", " + j + "] = "
								+ C[j * stride + i]
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