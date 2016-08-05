package org.jcrete.gemm.java;

public final class Main {

	private Main() {
	}

	public static void main(String[] args) {
		new Main().run();
	}

	private void run() {
		GEMM gemm = new GEMM();
		gemm.exec();
	}

}