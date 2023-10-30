package com.raffa064.engine.environments.editor;

import java.util.List;
import java.util.ArrayList;

public class EditorCore {
	private static EditorCore singleton;

	public static final int GET_PROJECT_DIR = 1;
	public static final int GET_PROJECT_CONFIGS = 2;
	public static final int GET_IS_EXPORTING_PROJECT = 3;
	public static final int GET_IS_IN_CODE_EDITOR = 4;

	public static final int EVENT_ERROR = 1;
	public static final int EVENT_CHANGE_ENGINE_DIR = 2;
	public static final int EVENT_OPEN_PROJECT = 3;
	public static final int EVENT_RELOAD_PROJECT = 4;
	public static final int EVENT_EXPORT_PROJECT = 5;
	public static final int EVENT_INSTALL_PROJECT = 6;
	public static final int EVENT_OPEN_CODE_EDITOR = 7;
	public static final int EVENT_CODE_EDITOR_CLOSED = 8;
	public static final int EVENT_OPEN_SCENE_TREE = 9;
	public static final int EVENT_OPEN_INSPECTOR = 10;

	private List<Module> moduleList = new ArrayList<>();

	private EditorCore() {}

	public void add(Module module) {
		module.core = this;
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
		public EditorCore core;
		
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
