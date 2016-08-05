package org.jcrete.gemm.opencl;

import org.lwjgl.PointerBuffer;
import org.lwjgl.opencl.CL;
import org.lwjgl.opencl.CLCapabilities;
import org.lwjgl.opencl.CLContextCallback;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.opencl.CL10.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public final class Main {

	private Main() {
	}

	public static void main(String[] args) {
		new Main().run();
	}

	private void run() {
		try (MemoryStack stack = stackPush()) {
			IntBuffer pi = stack.mallocInt(1);
			InfoUtil.checkCLError(clGetPlatformIDs(null, pi));
			if ( pi.get(0) == 0 )
				throw new RuntimeException("No OpenCL platforms found.");

			PointerBuffer platforms = stack.mallocPointer(pi.get(0));
			InfoUtil.checkCLError(clGetPlatformIDs(platforms, (IntBuffer)null));

			PointerBuffer ctxProps = stack.mallocPointer(3);
			ctxProps
				.put(0, CL_CONTEXT_PLATFORM)
				.put(2, 0);

			IntBuffer pErrcode = stack.callocInt(1);
			for ( int p = 0; p < platforms.capacity(); p++ ) {
				long platform = platforms.get(p);
				ctxProps.put(1, platform);

				System.out.println("\n-------------------------");
				System.out.printf("NEW PLATFORM: [0x%X]\n", platform);

				CLCapabilities platformCaps = CL.createPlatformCapabilities(platform);

				InfoUtil.checkCLError(clGetDeviceIDs(platform, CL_DEVICE_TYPE_CPU, null, pi));

				PointerBuffer devices = stack.mallocPointer(pi.get(0));
				InfoUtil.checkCLError(clGetDeviceIDs(platform, CL_DEVICE_TYPE_CPU, devices, (IntBuffer)null));

				for ( int d = 0; d < devices.capacity(); d++ ) {
					long device = devices.get(d);
					CLCapabilities caps = CL.createDeviceCapabilities(device, platformCaps);

					System.out.printf("\n\t** NEW DEVICE: [0x%X]\n", device);

					CLContextCallback contextCB;
					long context = clCreateContext(ctxProps, device, contextCB = CLContextCallback.create((errinfo, private_info, cb, user_data) -> {
						System.err.println("[LWJGL] cl_context_callback");
						System.err.println("\tInfo: " + memUTF8(errinfo));
					}), NULL, pErrcode);
					InfoUtil.checkCLError(pErrcode);

					long queue = clCreateCommandQueue(context, device, NULL, pErrcode);
					InfoUtil.checkCLError(pErrcode);

					demo(platformCaps, caps, device, context, queue);

					InfoUtil.checkCLError(clReleaseCommandQueue(queue));
					InfoUtil.checkCLError(clReleaseContext(context));
					contextCB.free();
				}
			}
		}
	}

	private void demo(
		CLCapabilities platformCaps,
		CLCapabilities deviceCaps,
		long device,
		long context,
		long queue
	) {
		GEMM gemm = new GEMM(device, context);
		gemm.exec(device, context, queue);
		gemm.destroy();
	}

}