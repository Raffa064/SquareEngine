package com.raffa064.engine.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.raffa064.engine.core.Scene;
import com.raffa064.engine.core.api.API;
import com.raffa064.engine.core.api.AssetsAPI;
import com.raffa064.engine.core.api.CollisionAPI;
import com.raffa064.engine.core.api.ComponentAPI;
import com.raffa064.engine.core.api.DebugAPI;
import com.raffa064.engine.core.api.GroupAPI;
import com.raffa064.engine.core.api.InputAPI;
import com.raffa064.engine.core.api.LoggerAPI;
import com.raffa064.engine.core.api.SceneAPI;
import com.raffa064.engine.core.api.TagAPI;
import com.raffa064.engine.core.collision.Shape;
import com.raffa064.engine.core.components.Native;
import com.raffa064.engine.core.components.StandardComponents;
import com.raffa064.engine.core.json.JSONLoader;
import com.raffa064.engine.environments.Android;
import com.raffa064.engine.environments.runtime.Encryptor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/*
	Topmost core class. It manage, and store all engine features.
*/

public class App {
	public ProjectConfigs configs;
	public FileHandle projectDir;
	public boolean isAbsolutePath; // switch between absolute/internal project folder
	public boolean autoTranspile = true;
	public boolean isEncrypted;
	public int decodeKey;

	public float viewportWidth = 1024;
	public float viewportHeight = 600;
	public boolean keepWidth = true;

	public Android android;
	public Scene currentScene, nextScene;
	public List<Native> apiInjectionList = new ArrayList<>();
	public JSONLoader jsonLoader;
	public HashMap<String, String> sceneFiles = new HashMap<>();
	public ScriptEngine scriptEngine;

	public List<API> apiList = new ArrayList<>();
	public DebugAPI Debug;
	public InputAPI Input;
	public CollisionAPI Collision;
	public GroupAPI Group;
	public TagAPI Tag;
	public SceneAPI Scene;
	public ComponentAPI Component;
	public AssetsAPI Assets;
	public LoggerAPI Logger;
	
	public App(Android android) {
		this.android = android;
	}

	public void loadProject(ProjectConfigs configs) throws Exception {
		this.configs = configs;
		
		projectDir = configs.getProjectDir();
		isAbsolutePath = configs.isAbsolutePath;
		autoTranspile = configs.autoTranspile;
		isEncrypted = configs.isEncrypted;
		decodeKey = configs.decodeKey;
					
		init();
		loadProjectFiles(projectDir); // recursvive load
		loadConfigs(configs);
	}

	public void init() {
		apiList.clear();

		// APIs
		Debug = new DebugAPI(this);
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
		setupScriptEngine();	
	}

	private void setupScriptEngine() {
		scriptEngine.setAutoTranspile(autoTranspile);
		
		// Injecting built-in functions
		String builtIn = Gdx.files.internal("built-in.js").readString();
		scriptEngine.compile(builtIn);

		// Injecting classes into script scope
		scriptEngine
			.injectClass(Color.class)
			.injectClass(Vector2.class)
			.injectClass(MathUtils.class)
			.injectClass(GlyphLayout.class)
			.injectClass(Rectangle.class)
			.injectClass(ShapeType.class)
			.injectClass(Shape.class)
			.injectClass(TextureRegion.class);

		// Injecting "export type" costants
		scriptEngine
			.inject("COLOR", "COLOR")
			.inject("STRING", "STRING")
			.inject("VECTOR2", "VECTOR2")
			.inject("FLOAT", "FLOAT")
			.inject("INTEGER", "INTEGER")
			.inject("BOOLEAN", "BOOLEAN")
			.inject("TEXTURE", "TEXTURE");

		// Injecting APIs 
		scriptEngine
			.inject("Debug", Debug)
			.inject("Input", Input)
			.inject("Collision", Collision)
			.inject("Group", Group)
			.inject("Tag", Tag)
			.inject("Scene", Scene)
			.inject("Component", Component.js())
			.inject("Assets", Assets)
			.inject("Logger", Logger);

		// Injecting native components
		Component.loadComponentList(StandardComponents.class);
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
					String code = file.readString();

					if (decodeKey != 0) {
						code = Encryptor.decrypt(code, decodeKey);
					}

					Component.loadScript(file.name(), code);
					break;
			}
		}
	}

	private void loadConfigs(ProjectConfigs configs) throws Exception {
		viewportWidth = configs.viewportWidth;
		viewportHeight = configs.viewportHeight;
		keepWidth = configs.keepWidth;
		
		Scene scene = loadScene(configs.mainScene);
		setScene(scene);
	}

	public FileHandle path(String path) {
		String inProjectPath = projectDir.path() + "/" + path;

		return isAbsolutePath ? 
			Gdx.files.absolute(inProjectPath) :
			Gdx.files.internal(inProjectPath);
	}

	public Scene loadScene(String name) throws Exception {
		String json = sceneFiles.get(name);
		Scene scn = jsonLoader.sceneFromJson(json);
		return scn;
	}

	public void setScene(Scene scene, boolean nextFrame) {
		if (nextFrame) {
			nextScene = scene;
			return;
		}

		currentScene = scene;
		android.setOrientation(currentScene.orientation);

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

	public void injectDependencies(Native component) {
		component.Debug = Debug;
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

    public void render(float delta) {
		Color backgroundColor = currentScene.backgroundColor;

		Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, backgroundColor.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		Input.update();

		currentScene.process(delta);
		Collision.stepPhysics(delta);
		Collision.renderDebug();

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
		scriptEngine.exit();
	}
}
