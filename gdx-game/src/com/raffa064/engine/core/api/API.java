package com.raffa064.engine.core.api;

import com.raffa064.engine.core.App;
import com.raffa064.engine.core.api.API.APIState;

/*
	An API is a class that provides a engine feature, like assets, graphics and logs
*/

public abstract class API {
    protected App app;

	public API(App app) {		
		if (app != null) {
			this.app = app;
			app.apiList.add(this);
		}
		
		createState();
	}
	
	public APIState buildState(Object... values) {
		APIState apiState = new APIState(values);
		return apiState;
	}
	
	public abstract APIState createState();
	
	public abstract void useState(APIState state);
	
	public static class APIState {
		public Object[] values;
		public int index;

		public APIState(Object[] values) {
			this.values = values;
		}
		
		public APIState first() {
			index = 0;
			
			return this;
		}
		
		public <T extends Object> T next() {
			return (T) values[index++];
		}
	}
}
