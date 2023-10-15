package com.raffa064.engine;

import java.util.List;
import java.util.ArrayList;

public class EditorCore {
	private static EditorCore singleton;

	public static final int GET_PROJECT_DIR = 1;
	public static final int GET_IS_EXPORTING_PROJECT = 2;

	public static final int EVENT_ERROR = 1;
	public static final int EVENT_RELOAD_PROJECT = 2;
	public static final int EVENT_EXPORT_PROJECT = 3;
	public static final int EVENT_INSTALL_PROJECT = 4;
	public static final int EVENT_OPEN_CODE = 5;
	public static final int EVENT_OPEN_SCENE_TREE = 6;
	public static final int EVENT_OPEN_INSPECTOR = 7;

	private List<Module> moduleList = new ArrayList<>();

	private EditorCore() {}

	public void add(Module module) {
		moduleList.add(module);
	}

	public void remove(Module module) {
		moduleList.remove(module);
	}

    public Object get(int action, Object... params) {
		for (Module module : moduleList) {
			Object result = module.onGet(action, params);

			if (result != null) {
				return result;
			}
		}

		return null;
	}

	public void event(int event, Object... params) {
		for (Module module : moduleList) {
			module.onEvent(event, params);
		}
	}

	public static interface Module {
		public Object onGet(int action, Object... params);

		public void onEvent(int event, Object... params);
	}

	public static EditorCore instance() {
		if (singleton == null) {
			singleton = new EditorCore();
		}

		return singleton;
	}
}
