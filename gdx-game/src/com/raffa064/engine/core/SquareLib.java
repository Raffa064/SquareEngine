package com.raffa064.engine.core;

/*
	It's a c/c++ library implementation
	
	NOTE: It's not used yet
*/

public class SquareLib {
    static {
		System.loadLibrary("square");
	}
	
	public native int soma(int a, int b);
}
