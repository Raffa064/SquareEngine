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

public class EditorGame extends BaseGame implements Module, ErrorListener {
	private boolean reloadRequest;
	private boolean isStable;

	public EditorGame(Android android) throws Exception {
		super(android);
		core = EditorCore.instance();
		core.add(this);
	}
	
	private void error(String message, Exception e) {
		core.event(EVENT_ERROR, message, e);
		isStable = false;
	}

	public boolean isStable() {
		return app != null && isStable;
	}

	@Override
	public void loadProject() {
		try {
			String projectPath = ((File) core.get(GET_PROJECT_DIR)).getAbsolutePath();
			configs = new ProjectConfigs(projectPath);
			
			App newApp = new App(android);
			newApp.loadProject(configs);
			newApp.scriptEngine.setErrorListener(this);

			if (app != null) {
				app.dispose(); // Dispose old instance
			}

			app = newApp;
			isStable = true;
		} catch (Exception e) {
			error("Error on load project: %s", e);
		}
	}
	
	@Override
	public void error(String message) {
		error(message, null);
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
				app.render(Gdx.graphics.getDeltaTime());
			} catch (Exception e) {
				error("Error on render frame: %s", e);
			}
		}
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
	public void resize(int width, int height) {
		if (isStable()) {
			try {
				app.resize(width, height);
			} catch (Exception e) {
				error("Error on resize: %s", e);
			}
		}
	}

	@Override
	public void dispose() {
		core.remove(this);
		
		if (isStable()) {
			try {
				app.dispose();
			} catch (Exception e) {
				error("Error on dispose: %s", e);
			}
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
			case EVENT_RELOAD_PROJECT: 
				reloadRequest = true; 
				break;
		}
	}
}
