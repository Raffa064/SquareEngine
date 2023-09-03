package com.raffa064.engine;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.raffa064.engine.core.App;

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

			this.app = app;
		} catch(Exception e) {
			if (reloadErr == null) {
				reloadErr = "REALOAD ERROR\nPROJECT PATH: "+ path + "\n" + getDetailedError(e);
				unstable = true;
			}
		}
	}
	
	public String getDetailedError(Exception e) {
		String err = "Error: "+e.getMessage() + "\n";
		
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
			
			return;
		}
		
		try {
			if (app != null) app.render(Gdx.graphics.getDeltaTime());
		} catch(Exception e) {
			Gdx.gl.glClearColor(1, 0, 0, 0);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
			
			if (renderErr == null) {
				renderErr = "RENDER ERROR\n" + getDetailedError(e);
				unstable = true;
			}
		}
	}

	boolean lastD;
	private void debugComands() {
		boolean isPressedD = Gdx.input.isKeyPressed(Input.Keys.D);

		if ((!lastD && isPressedD) || (Gdx.input.isTouched(0) && Gdx.input.isTouched(1) && Gdx.input.getDeltaX(0) < -10 && Gdx.input.getDeltaX(1) < -10)) {
			android.openEditor();
		}

		lastD = isPressedD;

		if (Gdx.input.isKeyPressed(Input.Keys.C)) {
			clearErrors();
		}
	}

	private void clearErrors() {
		reloadErr = renderErr = resizeErr = null;
		android.setDebugText("");
	}

	@Override
	public void resize(int width, int height) {
		try {
			if (app != null) app.resize(width, height);
		} catch(Exception e) {
			if (resizeErr == null) {
				resizeErr = "RESIZE ERROR\n"+getDetailedError(e);
				unstable = true;
			}
		}
	}
}
