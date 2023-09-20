package com.raffa064.engine;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.raffa064.engine.core.App;
import com.raffa064.engine.core.Scene;
import com.raffa064.engine.core.api.InputAPI;
import com.raffa064.engine.core.api.SceneAPI;

public class DebugGame extends Game {
	private AndroidInterface android;

	private String projectPath;
	private App app;

	private String reloadErr, renderErr, resizeErr;
	private boolean unstable;

	public DebugGame(AndroidInterface android) {
		this.android = android;
	}

	public void requestReload(String projectPath) {
		this.projectPath = projectPath;
	}

	public void reload(String path) {
		try {
			App app = new App();
			app.loadProject(Gdx.files.absolute(path));

			if (this.app != null) {
				this.app.dispose();
			}

			this.app = app;
		} catch (Exception e) {
			if (reloadErr == null) {
				reloadErr = "RELOAD ERROR\nPROJECT PATH: " + path + "\n" + getDetailedError(e);
				unstable = true;
			}
		}
	}

	public String getDetailedError(Exception e) {
		String err = "Error: " + e.getMessage() + "\n";

		for (StackTraceElement ste : e.getStackTrace()) {
			err += ste + "\n";
		}

		return err;
	}

	@Override
	public void create() {
		reload(android.getProjectPath());
	}

	@Override
	public void render() {
		if (projectPath != null) {
			unstable = false;
			clearErrors();
			reload(projectPath);
			projectPath = null;
		}

		debugComands();

		if (unstable) {
			showErrors();
			return;
		}

		try {
			if (!android.isEditorMode() && app != null) {
				app.render(Gdx.graphics.getDeltaTime());
			}

			if (android.isEditorMode()) {
				android.setDebugText("[ Editor mode ]");
			} else {
				android.setDebugText("");
			}
		} catch (Exception e) {
			Gdx.gl.glClearColor(1, 0, 0, 0);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

			if (renderErr == null) {
				renderErr = "RENDER ERROR\n" + getDetailedError(e);
				unstable = true;
			}
		}
	}

	private void showErrors() {
		String[] debug = new String[] {
			reloadErr,
			renderErr,
			resizeErr
		};

		for (int i = 0; i < debug.length; i++) {
			if (debug[i] != null) {
				android.setDebugText(debug[i]);
				break;
			}
		}
	}

	private void clearErrors() {
		reloadErr = renderErr = resizeErr = null;
		android.setDebugText("");
	}

	private void debugComands() {
		InputAPI Input = app.Input;
		SceneAPI Scene = app.Scene;

		boolean editorGesture = Input.deltaY(0) > 50 && Input.deltaY(1) > 50 && Input.touches() == 2;
		boolean sceneTreeGesture = Input.x(0) < Scene.width() / 2 && Input.x(1) < Scene.width() / 2 && Input.deltaX(0) > 10 && Input.deltaX(1) > 10 && Input.touches() == 2;
		boolean inspectorGesture = Input.x(0) > Scene.width() / 2 && Input.x(1) > Scene.width() / 2 && Input.deltaX(0) < -10 && Input.deltaX(1) < -10 && Input.touches() == 2;

		if (!android.isOpennedEditor() && editorGesture) {
			android.openEditor();
		}

		if (sceneTreeGesture) {
			android.openSceneTree();
		}

		if (inspectorGesture) {
			android.openInspector();
		}
	}

	@Override
	public void resize(int width, int height) {
		try {
			if (app != null) app.resize(width, height);
		} catch (Exception e) {
			if (resizeErr == null) {
				resizeErr = "RESIZE ERROR\n" + getDetailedError(e);
				unstable = true;
			}
		}
	}

	@Override
	public void dispose() {
		try {
			if (app != null) app.dispose();
		} catch (Exception e) {
			if (resizeErr == null) {
				resizeErr = "DISPOSE ERROR\n" + getDetailedError(e);
				unstable = true;
			}
		}
	}
}
