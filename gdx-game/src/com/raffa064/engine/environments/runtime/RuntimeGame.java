package com.raffa064.engine.environments.runtime;

import com.badlogic.gdx.Gdx;
import com.raffa064.engine.core.App;
import com.raffa064.engine.core.ProjectConfigs;
import com.raffa064.engine.core.ScriptEngine.ErrorListener;
import com.raffa064.engine.core.SquareLib;
import com.raffa064.engine.environments.Android;
import com.raffa064.engine.environments.BaseGame;
import org.mozilla.javascript.EvaluatorException;

/*
	This is the code that runs the game on exported APK files
*/

public class RuntimeGame extends BaseGame {
	public boolean stopped;
	
	public RuntimeGame(Android android) {
		super(android);
	}

	public void stop() {
		this.stopped = true;
	}

	public boolean isStopped() {
		return stopped;
	}

	@Override
	public void loadProject() {
		try {
			app = new App(android);
			app.loadProject(configs);
			app.scriptEngine.setErrorListener(this);
		} catch(Exception e) {
			android.error("Error on load project\nAn unexpected error occured when loading project:\n%s", e);
		}
	}
	
	@Override
	public void render() {
		if (isStopped()) return;
		
		try {
			app.render(Gdx.graphics.getDeltaTime());
		} catch(Exception e) {
			android.error("Error on render frame\nSome error occurred when rendering frame:\n%s", e);
		}
	}

	@Override
	public void resize(int width, int height) {
		if (isStopped()) return;
		
		try {
			app.resize(width, height);
		} catch(Exception e) {
			android.error("Error on resize\nError while resize window:\n%s", e);
		}
	}

	@Override
	public void dispose() {
		try {
			app.dispose();
		} catch(Exception e) {
			android.error("Error on dispose\nSomething went wrong while disposing resources:\n%s", e);
		}
	}
}
