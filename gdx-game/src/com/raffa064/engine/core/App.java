package com.raffa064.engine.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.raffa064.engine.core.api.AssetsAPI;
import com.raffa064.engine.core.api.ComponentAPI;
import com.raffa064.engine.core.api.GroupAPI;
import com.raffa064.engine.core.api.LoggerAPI;
import com.raffa064.engine.core.api.SceneAPI;
import com.raffa064.engine.core.api.TagAPI;
import com.raffa064.engine.core.components.Native;
import com.raffa064.engine.core.Scene;
import java.util.HashMap;
import java.util.List;
import org.json.JSONObject;
import java.util.ArrayList;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Rectangle;
import com.raffa064.engine.core.api.CollisionAPI;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.raffa064.engine.core.api.InputAPI;
import com.raffa064.engine.core.api.API;

public class App {
	public float viewportWidth = 1024;
	public float viewportHeight = 600;
	public boolean keepWidth = true;

	public FileHandle projectFolder;
	public Scene currentScene, nextScene;
	public List<Native> apiInjectionList = new ArrayList<>();
	public JSONLoader jsonLoader;
	public HashMap<String, String> sceneFiles = new HashMap<>();
	public ScriptEngine scriptEngine;

	public List<API> apiList = new ArrayList<>();
	public InputAPI Input;
	public CollisionAPI Collision;
	public GroupAPI Group;
	public TagAPI Tag;
	public SceneAPI Scene;
	public ComponentAPI Component;
	public AssetsAPI Assets;
	public LoggerAPI Logger;

	public FileHandle relative(String path) {
		return Gdx.files.absolute(projectFolder.path() + "/" + path);
	}

	public void setScene(Scene scene, boolean nextFrame) {
		if (nextFrame) {
			nextScene = scene;
			return;
		}
		
		currentScene = scene;
		
		scene.setApp(this);
		scene.init();

		scriptEngine
			.inject("batch", scene.batch)
			.inject("shape", scene.shape);

		Scene.history.add(scene);
	}
	
	public void setScene(Scene scene) {
		setScene(scene, false);
	}

	public void init() {
		Input = new InputAPI(this);
		Collision = new CollisionAPI(this);
		Group = new GroupAPI(this);
		Tag = new TagAPI(this);
		Scene = new SceneAPI(this);
		Component = new ComponentAPI(this);
		Assets = new AssetsAPI(this);
		Logger = new LoggerAPI(this);

		jsonLoader = new JSONLoader(this);
		scriptEngine = new ScriptEngine();

		scriptEngine
			.injectClass(Color.class)
			.injectClass(Vector2.class)
			.injectClass(MathUtils.class)
			.injectClass(GlyphLayout.class)
			.injectClass(Rectangle.class)
			.injectClass(ShapeType.class);

		scriptEngine
			.inject("COLOR", "COLOR")
			.inject("STRING", "STRING")
			.inject("VECTOR2", "VECTOR2")
			.inject("FLOAT", "FLOAT")
			.inject("INTEGER", "INTEGER")
			.inject("TEXTURE", "TEXTURE")
			.inject("GAME_OBJECT", "GAME_OBJECT");

		scriptEngine
			.inject("Input", Input)
			.inject("Collision", Collision)
			.inject("Group", Group)
			.inject("Tag", Tag)
			.inject("Scene", Scene)
			.inject("Component", Component.js())
			.inject("Assets", Assets)
			.inject("Logger", Logger);
	}

	public void injectDependencies(Native component) {
		component.Input = Input;
		component.Collision = Collision;
		component.Group = Group;
		component.Tag = Tag;
		component.Scene = Scene;
		component.Component = Component;
		component.Assets = Assets;
		component.Logger = Logger;

		component.batch = currentScene.batch;
		component.shape = currentScene.shape;
	}

	public Scene loadScene(String name) throws Exception {
		Scene scn = jsonLoader.sceneFromJson(sceneFiles.get(name));
		return scn;
	}

	public void loadProject(FileHandle folder) throws Exception {
		projectFolder = folder;

		init();
		loadProjectFiles(folder);
		loadConfigs();
	}

	private void loadProjectFiles(FileHandle folder) {
		for (FileHandle file : folder.list()) {
			if (file.isDirectory()) {
				loadProjectFiles(file);
				continue;
			} 

			String extension = file.extension();
			switch (extension) {
				case "scn":
					sceneFiles.put(file.nameWithoutExtension(), file.readString());
					break;
				case "js":
					Component.loadScript(file.name(), file.readString());
					break;
			}
		}
	}

	private void loadConfigs() throws Exception {
		JSONObject config =  new JSONObject(Assets.readFile("config.cfg"));
		JSONObject viewport = config.getJSONObject("viewport");

		viewportWidth = (float) viewport.getDouble("width");
		viewportHeight = (float) viewport.getDouble("height");
		keepWidth = viewport.getBoolean("keepWidth");

		String mainScene = config.getString("mainScene");
		Scene scene = loadScene(mainScene);
		setScene(scene);
	}

    public void render(float delta) {
		Color backgroundColor = currentScene.backgroundColor;

		Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, backgroundColor.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
		Input.update();

		currentScene.process(delta);
		
		if (nextScene != null) {
			setScene(nextScene);
			nextScene = null;
		}
	}

	public void resize(int width, int height) {
		currentScene.setupCamera(width, height);
	}
	
	public void dispose() {
		Assets.dispose();
	}
}
