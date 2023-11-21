package com.raffa064.engine.modules;

import android.app.Activity;
import com.raffa064.engine.R;
import com.raffa064.engine.data.INI;
import com.raffa064.engine.environments.editor.EditorCore.Module;
import java.io.File;
import java.io.IOException;

import static com.raffa064.engine.environments.editor.EditorCore.*;

/*
	This module will provide access to engine data,
	including:
	-	configurations;
	-	history;
	-	custom keystores;
	-	and other things.
*/

public class EngineDataModule implements Module {
	public Activity activity;
	public File dataDir;
	public INI configs;

	public EngineDataModule(Activity activity) {
		this.activity = activity;
	}
	
	@Override
	public Object onGet(int action, Object[] params) {
		switch (action) {
			case GET_LAST_OPENNED_PROJECT:
				return configs.file("editor", "lastProject");
		}
		
		return null;
	}

	@Override
	public void onEvent(int event, Object[] params) {
		switch (event) {
			case EVENT_CHANGE_ENGINE_DATA_DIR:
				changeEngineDataDir((File) params[0]);
				break;
			case EVENT_OPEN_PROJECT:
				openProject((File) params[0]);
				break;
		}
	}
	
	private void changeEngineDataDir(File dataDir) {
		this.dataDir = dataDir;
		
		if (!dataDir.exists()) {
			dataDir.mkdir();
		}

		if (!dataDir.exists()) {
			throw new Error("Engine data directory doesn't exists");
		}
		
		try {
			File configsFile = new File(dataDir, "configs.ini");
			configs = new INI(configsFile);
		} catch (IOException e) {
			throw new Error("Error on load configs file");
		}
	}
	
	private void openProject(File project) {
		try {
			configs.file("editor", "lastProject", project).commit();
		} catch (IOException e) {
			throw new Error("Error on commit changes to 'lastProject'");
		}
	}
}
