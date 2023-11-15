package com.raffa064.engine.core.api;

import com.raffa064.engine.core.App;

/*
	Used to make logs
*/

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
		String str = "";
		
		for (Object p : params) {
			str += p;
		}
		
		app.android.debug("Debug log"+str);
	}
}
