/*
 * Copyright LWJGL. All rights reserved.
 * License terms: http://lwjgl.org/license.php
 */
package org.jcrete.gemm.opencl;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;

import static org.lwjgl.opencl.CL10.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

/**
 * OpenCL object info utilities.
 */
final class InfoUtil {

	private InfoUtil() {
	}

	static String getPlatformInfoStringASCII(long cl_platform_id, int param_name) {
		try (MemoryStack stack = stackPush()) {
			PointerBuffer pp = stack.mallocPointer(1);
			checkCLError(clGetPlatformInfo(cl_platform_id, param_name, (ByteBuffer)null, pp));
			int bytes = (int)pp.get(0);

			ByteBuffer buffer = stack.malloc(bytes);
			checkCLError(clGetPlatformInfo(cl_platform_id, param_name, buffer, null));

			return memASCII(buffer, bytes - 1);
		}
	}

	static String getPlatformInfoStringUTF8(long cl_platform_id, int param_name) {
		try (MemoryStack stack = stackPush()) {
			PointerBuffer pp = stack.mallocPointer(1);
			checkCLError(clGetPlatformInfo(cl_platform_id, param_name, (ByteBuffer)null, pp));
			int bytes = (int)pp.get(0);

			ByteBuffer buffer = stack.malloc(bytes);
			checkCLError(clGetPlatformInfo(cl_platform_id, param_name, buffer, null));

			return memUTF8(buffer, bytes - 1);
		}
	}

	static int getDeviceInfoInt(long cl_device_id, int param_name) {
		try (MemoryStack stack = stackPush()) {
			IntBuffer pl = stack.mallocInt(1);
			checkCLError(clGetDeviceInfo(cl_device_id, param_name, pl, null));
			return pl.get(0);
		}
	}

	static long getDeviceInfoLong(long cl_device_id, int param_name) {
		try (MemoryStack stack = stackPush()) {
			LongBuffer pl = stack.mallocLong(1);
			checkCLError(clGetDeviceInfo(cl_device_id, param_name, pl, null));
			return pl.get(0);
		}
	}

	static long getDeviceInfoPointer(long cl_device_id, int param_name) {
		try (MemoryStack stack = stackPush()) {
			PointerBuffer pp = stack.mallocPointer(1);
			checkCLError(clGetDeviceInfo(cl_device_id, param_name, pp, null));
			return pp.get(0);
		}
	}

	static String getDeviceInfoStringUTF8(long cl_device_id, int param_name) {
		try (MemoryStack stack = stackPush()) {
			PointerBuffer pp = stack.mallocPointer(1);
			checkCLError(clGetDeviceInfo(cl_device_id, param_name, (ByteBuffer)null, pp));
			int bytes = (int)pp.get(0);

			ByteBuffer buffer = stack.malloc(bytes);
			checkCLError(clGetDeviceInfo(cl_device_id, param_name, buffer, null));

			return memUTF8(buffer, bytes - 1);
		}
	}

	static long getMemObjectInfoPointer(long cl_mem, int param_name) {
		try (MemoryStack stack = stackPush()) {
			PointerBuffer pp = stack.mallocPointer(1);
			checkCLError(clGetMemObjectInfo(cl_mem, param_name, pp, null));
			return pp.get(0);
		}
	}

	static long getMemObjectInfoInt(long cl_mem, int param_name) {
		try (MemoryStack stack = stackPush()) {
			IntBuffer pi = stack.mallocInt(1);
			checkCLError(clGetMemObjectInfo(cl_mem, param_name, pi, null));
			return pi.get(0);
		}
	}

	static int getProgramBuildInfoInt(long cl_program_id, long cl_device_id, int param_name) {
		try (MemoryStack stack = stackPush()) {
			IntBuffer pl = stack.mallocInt(1);
			checkCLError(clGetProgramBuildInfo(cl_program_id, cl_device_id, param_name, pl, null));
			return pl.get(0);
		}
	}

	static String getProgramBuildInfoStringASCII(long cl_program_id, long cl_device_id, int param_name) {
		try (MemoryStack stack = stackPush()) {
			PointerBuffer pp = stack.mallocPointer(1);
			checkCLError(clGetProgramBuildInfo(cl_program_id, cl_device_id, param_name, (ByteBuffer)null, pp));
			int bytes = (int)pp.get(0);

			ByteBuffer buffer = stack.malloc(bytes);
			checkCLError(clGetProgramBuildInfo(cl_program_id, cl_device_id, param_name, buffer, null));

			return memASCII(buffer, bytes - 1);
		}
	}

	static void checkCLError(IntBuffer pErrcode) {
		checkCLError(pErrcode.get(pErrcode.position()));
	}

	static void checkCLError(int errcode) {
		if ( errcode != CL_SUCCESS )
			throw new RuntimeException(String.format("OpenCL error [%d]", errcode));
	}

}