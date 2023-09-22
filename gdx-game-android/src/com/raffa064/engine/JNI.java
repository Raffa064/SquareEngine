package com.raffa064.engine;

public class JNI {
	public JNI() {
		System.loadLibrary("minha_biblioteca");
	}

	public native int soma(int a, int b);
	
	public native int pow(int a, int b);
}
