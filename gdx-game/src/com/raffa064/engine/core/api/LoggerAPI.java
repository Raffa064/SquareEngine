package com.raffa064.engine.core.api;

public class LoggerAPI {
	public void log(Object... params) {
		for (Object p : params) {
			System.out.print(p);
		}
		
		System.out.println();
	}
}
