package com.raffa064.engine;

import android.app.Application;
import android.content.res.Configuration;

public class EditorApplication extends Application {
    private AndroidJSI androidJSI;
	
	public void initJSI(String projectPath/* Params: DebugGame, App, etc... */) {
		androidJSI = new AndroidJSI();
		androidJSI.setFolderPath(projectPath);
	}
	
	public void setAndroidJSI(AndroidJSI androidJSI) {
		this.androidJSI = androidJSI;
	}

	public AndroidJSI getAndroidJSI() {
		return androidJSI;
	}
}
