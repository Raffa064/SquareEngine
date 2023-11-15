package com.raffa064.engine.core;

public interface OutputHandler {
	public void debug(String message);
	public void warning(String warning);
	public void error(String message, Throwable error);
}
