package com.raffa064.engine.environments.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.raffa064.engine.core.App;
import com.raffa064.engine.core.ProjectConfigs;
import com.raffa064.engine.core.ScriptEngine.ErrorListener;
import com.raffa064.engine.environments.Android;
import com.raffa064.engine.environments.BaseGame;
import java.io.File;
import org.mozilla.javascript.EvaluatorException;

import static com.raffa064.engine.environments.editor.EditorCore.*;
import com.raffa064.engine.core.OutputHandler;

/*
	This is the in-editor game. It provides some debug and edition stuff.
*/

public class EditorGame extends BaseGame implements Module, ErrorListener {
	private boolean reloadRequest;
	private boolean isStable;
	private int turbo;

	public EditorGame(Android android) throws Exception {
		super(android);
		core = EditorCore.instance();
		core.add(this);
	}
	
	public boolean isStable() {
		return app != null && isStable;
	}
	
	private void keyboardShortcuts() {
		if (Gdx.input.isKeyPressed(Input.Keys.F1)) {
			core.event(EditorCore.EVENT_EXPORT_PROJECT);
		}

		if (Gdx.input.isKeyPressed(Input.Keys.F2)) {
			core.event(EditorCore.EVENT_INSTALL_PROJECT);
		}

		if (Gdx.input.isKeyPressed(Input.Keys.F3)) {
			core.event(EditorCore.EVENT_OPEN_CODE_EDITOR);
		}

		if (Gdx.input.isKeyPressed(Input.Keys.F4)) {
			core.event(EditorCore.EVENT_RELOAD_PROJECT);
		}
	}

	@Override
	public void loadProject() {
		try {
			String projectPath = ((File) core.get(GET_PROJECT_DIR)).getAbsolutePath();
			configs = new ProjectConfigs(projectPath);
			
			App newApp = new App(android);
			newApp.editorMode = true;
			newApp.loadProject(configs);
			newApp.scriptEngine.setErrorListener(this);

			if (app != null) {
				app.dispose(); // Dispose old instance
			}

			app = newApp;
			isStable = true;
		} catch (Exception e) {
			android.error("Error on load project\nAn unexpected error occured when loading project:\n%s", e);
		}
	}
	
	@Override
	public void render() {
		boolean isExporting = (boolean) core.get(EditorCore.GET_IS_EXPORTING_PROJECT);
		
		if (isExporting) {
			return;
		}
		
		keyboardShortcuts();
		
		if (reloadRequest) {
			reloadRequest = false;
			loadProject();
		}

		if (isStable()) {
			try {
				int fpsMultiplier = turbo + 1;
				for (int i = 0; i < fpsMultiplier; i++) {
					app.render(Gdx.graphics.getDeltaTime()/fpsMultiplier);
				}
			} catch (Exception e) {
				android.error("Error on render frame\nSome error occurred when rendering frame:\n%s", e);
			}
		}
	}

	@Override
	public void resize(int width, int height) {
		if (isStable()) {
			try {
				app.resize(width, height);
			} catch (Exception e) {
				android.error("Error on resize\nError while resize window:\n%s", e);
			}
		}
	}

	@Override
	public void dispose() {
		core.remove(this);
		
		try {
			app.dispose();
		} catch (Exception e) {
			android.error("Error on dispose\nSomething went wrong while disposing resources:\n%s", e);
		}
	}
	
	@Override
	public Object onGet(int action, Object[] params) {
		switch(action) {
			case GET_PROJECT_CONFIGS: return configs;
		}
		
		return null;
	}

	@Override
	public void onEvent(int event, Object[] params) {
		switch (event) {
			case EVENT_ERROR:
				isStable = false;
				break;
			case EVENT_RELOAD_PROJECT: 
				reloadRequest = true; 
				break;
			case EVENT_TOGGLE_TURBO:
				turbo = ++turbo % 10;
				break;
		}
	}
}
