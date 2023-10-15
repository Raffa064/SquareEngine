package com.raffa064.engine;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.raffa064.engine.core.App;
import com.raffa064.engine.core.SquareLib;

public class DebugGame /*extends Game*/ {
	/*private EditorInterface editor;

	private String projectPath;
	private App app;

	private String reloadErr, renderErr, resizeErr;
	private boolean unstable;

	private SquareLib lib = new SquareLib();
	
	public DebugGame(EditorInterface android) {
		this.editor = android;
	}

	public void requestReload(String projectPath) {
		this.projectPath = projectPath;
	}

	public void reload(String path) {
		try {
			App app = new App();
			app.loadProject(path, true);

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
		reload(editor.getProjectPath());
	}

	public void renderExporting() {
		Gdx.gl.glClearColor(.2f, .2f, .2f, 1.f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		editor.setDebugText("Exporting...");
	}

	@Override
	public void render() {
		if (projectPath != null) {
			unstable = false;
			clearErrors();
			reload(projectPath);
			projectPath = null;
		}

		if (editor.isExporting()) {
			renderExporting();
			return;
		}

		debugComands();

		if (unstable) {
			showErrors();
			return;
		}

		try {
			if (app != null) {
				app.render(Gdx.graphics.getDeltaTime());
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
				editor.setDebugText(debug[i]);
				break;
			}
		}
	}

	private void clearErrors() {
		reloadErr = renderErr = resizeErr = null;
		editor.setDebugText("");
	}

	private void debugComands() {
		boolean editorGesture = Gdx.input.getDeltaY(0) > 50 && Gdx.input.getDeltaY(1) > 50 && Gdx.input.isTouched(0) && Gdx.input.isTouched(1);
		boolean sceneTreeGesture = Gdx.input.getX(0) < Gdx.graphics.getWidth() / 2 && Gdx.input.getX(1) < Gdx.graphics.getWidth() / 2 && Gdx.input.getDeltaX(0) > 10 && Gdx.input.getDeltaX(1) > 10 && Gdx.input.isTouched(0) && Gdx.input.isTouched(1);
		boolean inspectorGesture = Gdx.input.getX(0) > Gdx.graphics.getWidth() / 2 && Gdx.input.getX(1) > Gdx.graphics.getWidth() / 2 && Gdx.input.getDeltaX(0) < -10 && Gdx.input.getDeltaX(1) < -10 && Gdx.input.isTouched(0) && Gdx.input.isTouched(1);

		if (!editor.isOpennedEditor() && editorGesture) {
			editor.openEditor();
		}

		if (sceneTreeGesture) {
			editor.openSceneTree();
		}

		if (inspectorGesture) {
			editor.openInspector();
		}

		if (app != null) {
			if (app.Input.keyPressed(app.Input.F1)) {
				editor.exportProject();
			}
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
	*/
}
