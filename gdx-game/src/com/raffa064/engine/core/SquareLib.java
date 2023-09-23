package com.raffa064.engine.core;

public class SquareLib {
    static {
		System.loadLibrary("square");
	}
	
	public native int soma(int a, int b);
}
