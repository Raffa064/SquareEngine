package com.raffa064.engine.core.api;

import com.raffa064.engine.core.App;

public class LoggerAPI extends API {
	public LoggerAPI(App app) {
		super(app);
	}
	
	public void log(Object... params) {
		for (Object p : params) {
			System.out.print(p);
		}
		
		System.out.println();
	}
}
