package com.raffa064.engine.core.api;

import com.raffa064.engine.core.App;
import com.raffa064.engine.core.GameObject;

public class EngineAPI extends API {
	public EngineAPI(App app) {
		super(app);
	}
	
	@Override
	public API.APIState createState() {
		return buildState();
	}

	@Override
	public void useState(API.APIState state) {
	}

	public boolean editor() {
		return app.editorMode;
	}
	
	public boolean focusIn(GameObject obj) {
		if (obj == app.focusedObject) { // TODO: check if obj is selected obj
			return true;
		}
		
		return false;
	}
	
	public void requestFocus(GameObject obj) {
		app.focusedObject = obj; // Can be null (when it's unselecting)
	}

	public int version() {
		return App.VERSION;
	}
	
	public String versionName() {
		return App.VERSION_NAME;
	}
}
