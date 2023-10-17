package com.raffa064.engine;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.raffa064.engine.core.App;
import com.raffa064.engine.core.ProjectConfigs;
import com.raffa064.engine.core.ScriptEngine.ErrorListener;
import java.io.File;
import org.mozilla.javascript.EvaluatorException;

import static com.raffa064.engine.EditorCore.*;

public class EditorGame extends Game implements Module, ErrorListener {
	private EditorCore editor;
	private ProjectConfigs configs;
	private App app;
	private boolean reloadRequest;
	private boolean isStable;

	public EditorGame() throws Exception {
		editor = EditorCore.instance();
		editor.add(this);
	}

	public void loadProject() {
		try {
			String projectPath = ((File) editor.get(GET_PROJECT_DIR)).getAbsolutePath();
			configs = new ProjectConfigs(projectPath);
			
			App newApp = new App();
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

	private void error(String message, Exception e) {
		editor.event(EVENT_ERROR, message, e);
		isStable = false;
	}

	public boolean isStable() {
		return app != null && isStable;
	}
	
	public String trimLineSource(String lineSource, int offset) {
		return lineSource.substring(Math.max(0, offset - 10), Math.min(lineSource.length(), offset + 10));
	}

	@Override
	public void create() {
		loadProject();
	}

	@Override
	public void render() {
		boolean isExporting = (boolean) editor.get(EditorCore.GET_IS_EXPORTING_PROJECT);
		
		if (isExporting) {
			return;
		}
		
		if (Gdx.input.isKeyPressed(Input.Keys.F1)) {
			editor.event(EditorCore.EVENT_EXPORT_PROJECT);
		}

		if (Gdx.input.isKeyPressed(Input.Keys.F2)) {
			editor.event(EditorCore.EVENT_INSTALL_PROJECT);
		}

		if (Gdx.input.isKeyPressed(Input.Keys.F3)) {
			editor.event(EditorCore.EVENT_OPEN_CODE);
		}

		if (Gdx.input.isKeyPressed(Input.Keys.F4)) {
			editor.event(EditorCore.EVENT_RELOAD_PROJECT);
		}
		
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
	
	@Override
	public void warning(String message, String source, int lineNumber, String lineSource, int lineOffset) {
		error(String.format("Script Error (%s:%d): %s\n%s", source, lineNumber, message, trimLineSource(lineSource, lineOffset)), null);
	}

	@Override
	public void error(String message, String source, int lineNumber, String lineSource, int lineOffset) {
		error(String.format("Script Error (%s:%d): %s\n%s", source, lineNumber, message, trimLineSource(lineSource, lineOffset)), null);
	}

	@Override
	public EvaluatorException runtimeError(String message, String source, int lineNumber, String lineSource, int lineOffset) {
		String error = String.format("Script Error (%s:%d): %s\n%s", source, lineNumber, message, trimLineSource(lineSource, lineOffset));
		error(error, null);
		
		return new EvaluatorException(error);
	}
}
