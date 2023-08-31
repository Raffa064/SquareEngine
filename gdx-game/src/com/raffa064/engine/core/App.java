package com.raffa064.engine.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.raffa064.engine.core.api.Assets;
import com.raffa064.engine.core.api.Logger;
import com.raffa064.engine.core.components.Scene;
import com.raffa064.engine.core.api.ComponentLoader;
import com.badlogic.gdx.files.FileHandle;

public class App {
	public Color backgroundColor = Color.BLACK;
	public float viewportWidth = 1024;
	public float viewportHeight = 600;
	public boolean keepWidth = true;

	public Scene scene;
	public ScriptEngine scriptEngine;

	public ComponentLoader componentLoader;
	public Assets assets;
	public Logger logger;

	public void setScene(Scene scene) {
		this.scene = scene;
		scene.setApp(this);
		scene.init();

		scriptEngine
			.inject("batch", scene.batch)
			.inject("shape", scene.shape);
	}

	public void init() {
		componentLoader = new ComponentLoader(this);
		assets = new Assets();
		logger = new Logger();
		
		scriptEngine = new ScriptEngine();
		
		scriptEngine
		    .inject("Scene", new com.raffa064.engine.core.api.Scene(this))
			.inject("Component", componentLoader.js())
			.inject("Assets", assets)
			.inject("Logger", logger);
	}


	public void loadProject(FileHandle folder) {
		//TODO: load project.config, and setScene(mainScene)
		
		for (FileHandle file : folder.list()) {
			if (file.isDirectory()) {
				loadProject(folder);
				return;
			} 

			String extension = file.extension();
			switch (extension) {
				case "js":
					componentLoader.loadScript(file.name(), file.readString());
			}
		}
	}

    public void render(float delta) {
		Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, backgroundColor.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		scene.process(delta);
	}

	public void resize(int width, int height) {
		scene.setupCamera(width, height);
	}

	public SpriteBatch getSceneBatch() {
		return scene.batch;
	}

	public ShapeRenderer getSceneShapeRender() {
		return scene.shape;
	}
}
