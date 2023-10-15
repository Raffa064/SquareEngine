package com.raffa064.engine;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.raffa064.engine.core.App;
import java.io.File;

import static com.raffa064.engine.EditorCore.*;

public class EditorGame extends Game implements Module {
	private EditorCore editor;
	private App app;
	private boolean reloadRequest;
	private boolean isStable;

	public EditorGame() {
		editor = EditorCore.instance();
		editor.add(this);
	}

	public void loadProject() {
		try {
			App newApp = new App();
			File projectDir = (File) editor.get(GET_PROJECT_DIR);
			newApp.loadProject(projectDir, true);

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

	@Override
	public Object onGet(int action, Object[] params) {
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
}
