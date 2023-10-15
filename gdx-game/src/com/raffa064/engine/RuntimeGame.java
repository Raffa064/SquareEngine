package com.raffa064.engine;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.raffa064.engine.core.App;
import com.raffa064.engine.core.SquareLib;

public class RuntimeGame extends Game {
	private EditorInterface android;

	private App app;

	private SquareLib lib = new SquareLib();
	
	@Override
	public void create() {
		try {
			app = new App();
			app.loadProject("project", false);
		} catch(Exception e) {
		}
	}

	@Override
	public void render() {
		app.render(Gdx.graphics.getDeltaTime());
	}

	@Override
	public void resize(int width, int height) {
		app.resize(width, height);
	}

	@Override
	public void dispose() {
		app.dispose();
	}
}
