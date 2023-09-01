package com.raffa064.engine.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.raffa064.engine.core.api.AssetsAPI;
import com.raffa064.engine.core.api.ComponentAPI;
import com.raffa064.engine.core.api.LoggerAPI;
import com.raffa064.engine.core.api.SceneAPI;
import com.raffa064.engine.core.components.Native;
import com.raffa064.engine.core.components.Scene;
import com.raffa064.engine.core.api.TagAPI;

public class App {
	public float viewportWidth = 1024;
	public float viewportHeight = 600;
	public boolean keepWidth = true;

	public Scene currentScene;
	public ScriptEngine scriptEngine;

	public TagAPI Tag;
	public SceneAPI Scene;
	public ComponentAPI Component;
	public AssetsAPI Assets;
	public LoggerAPI Logger;

	public void setScene(Scene scene) {
		this.currentScene = scene;
		scene.setApp(this);
		scene.init();

		scriptEngine
			.inject("batch", scene.batch)
			.inject("shape", scene.shape);
	}

	public void init() {
		Tag = new TagAPI(this);
		Scene = new SceneAPI(this);
		Component = new ComponentAPI(this);
		Assets = new AssetsAPI();
		Logger = new LoggerAPI();

		scriptEngine = new ScriptEngine();
		
		scriptEngine
			.injectClass(Color.class)
			.injectClass(Vector2.class)
			.injectClass(MathUtils.class);

		scriptEngine
			.inject("COLOR", "COLOR")
			.inject("STRING", "STRING")
			.inject("VECTOR2", "VECTOR2")
			.inject("FLOAT", "FLOAT")
			.inject("INTEGER", "INTEGER")
			.inject("TEXTURE", "TEXTURE")
			.inject("GAME_OBJECT", "GAME_OBJECT");

		scriptEngine
			.inject("Tag", Tag)
			.inject("Scene", Scene)
			.inject("Component", Component.js())
			.inject("Assets", Assets)
			.inject("Logger", Logger);
	}
	
	public void injectAPIs(Native component) {
		component.Tag = Tag;
		component.Scene = Scene;
		component.Component = Component;
		component.Assets = Assets;
		component.Logger = Logger;
		
		component.batch = currentScene.batch;
		component.shape = currentScene.shape;
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
					Component.loadScript(file.name(), file.readString());
			}
		}
	}

    public void render(float delta) {
		Color backgroundColor = currentScene.backgroundColor;

		Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, backgroundColor.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		currentScene.process(delta);
	}

	public void resize(int width, int height) {
		currentScene.setupCamera(width, height);
	}

	public SpriteBatch getSceneBatch() {
		return currentScene.batch;
	}

	public ShapeRenderer getSceneShapeRender() {
		return currentScene.shape;
	}
}
