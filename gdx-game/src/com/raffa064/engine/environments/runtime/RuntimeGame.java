package com.raffa064.engine.environments.runtime;

import com.badlogic.gdx.Gdx;
import com.raffa064.engine.core.App;
import com.raffa064.engine.core.ProjectConfigs;
import com.raffa064.engine.core.ScriptEngine.ErrorListener;
import com.raffa064.engine.core.SquareLib;
import com.raffa064.engine.environments.Android;
import com.raffa064.engine.environments.BaseGame;
import org.mozilla.javascript.EvaluatorException;

public abstract class RuntimeGame extends BaseGame {
	public RuntimeGame(Android android) {
		super(android);
	}

	@Override
	public void loadProject() {
		try {
			app = new App(android);
			app.loadProject(configs);
			app.scriptEngine.setErrorListener(this);
		} catch(Exception e) {
			error("Load Error: "+e.getMessage());
		}
	}
	
	@Override
	public abstract void error(String message);
	
	@Override
	public void render() {
		try {
			app.render(Gdx.graphics.getDeltaTime());
		} catch(Exception e) {
			error("Render error: " + e.toString());
		}
	}

	@Override
	public void resize(int width, int height) {
		try {
			app.resize(width, height);
		} catch(Exception e) {
			error("Resize error: " + e.toString());
		}
	}

	@Override
	public void dispose() {
		try {
			app.dispose();
		} catch(Exception e) {
			error("Dispose error: " + e.toString());
		}
	}
}
