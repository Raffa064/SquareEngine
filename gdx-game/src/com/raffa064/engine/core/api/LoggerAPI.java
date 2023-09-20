package com.raffa064.engine.core.api;

import com.raffa064.engine.core.App;

public class LoggerAPI extends API {
	public LoggerAPI(App app) {
		super(app);
	}
	
	@Override
	public APIState createState() {
		return buildState();
	}


	@Override
	public void useState(APIState values) {
	}
	
	public void log(Object... params) {
		for (Object p : params) {
			System.out.print(p);
		}
		
		System.out.println();
	}
}
