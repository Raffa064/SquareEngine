package com.raffa064.engine.core.tests;

import com.raffa064.engine.core.App;
import com.raffa064.engine.core.GameObject;
import com.raffa064.engine.core.JSONLoader;
import com.raffa064.engine.core.Scene;
import com.raffa064.engine.core.ScriptEngine;
import com.raffa064.engine.core.api.ComponentAPI;
import com.raffa064.engine.core.components.StandardComponents;
import com.raffa064.engine.core.components.commons2d.Transform2D;
import java.io.File;
import java.io.FileInputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.raffa064.engine.core.api.LoggerAPI;

public class Main {
	public static void main(String[] args) throws Exception {
		test_transpiler2();
	}
	
	private static void test_transpiler2() {
		ScriptEngine engine = new ScriptEngine();
		
		engine.inject("logger", new LoggerAPI(null));
		
		String script = readFile("/storage/emulated/0/AppProjects/SquareEngine/gdx-game/src/com/raffa064/engine/core/tests/teste.js");
		String transpiledScript = engine.transpile(script);
		System.out.println(transpiledScript);
		System.out.println(transpiledScript.split("\n").length + " <- " + script.split("\n").length);
		
		engine.compile(script);
	}

	private static void test_name_counter() {
		String name = "test";

		Matcher matcher = Pattern.compile("\\([0-9]+\\)").matcher(name);

		if (matcher.find()) {
			int start = matcher.start();
			int end = matcher.end();

			int counter = Integer.parseInt(name.substring(start + 1, end - 1)) + 1;
			name = name.substring(0, start) + "(" + counter + ")" + name.substring(end, name.length());
		} else {
			name = name + " (1)";
		}

		System.out.println("COUNTER: " + name);
	}

	private static void test_transpiler() {
		ScriptEngine engine = new ScriptEngine();
		String script = readFile("/storage/emulated/0/AppProjects/GameEngine/gdx-game-android/assets/project/MoveComponent.js"); //"/storage/emulated/0/AppProjects/GameEngine/gdx-game/src/com/raffa064/engine/core/tests/teste.js");
		String transpile = engine.transpile(script);
		System.out.println(transpile);

		try {
			engine.compile(script);
			System.out.println("Sucess!");
		} catch (Exception e) {
			System.out.println("Transpiler error, or invalid test source");
		}
	}

	private static void test_SceneLoader() throws Exception {
		Scene scene = new Scene();
		scene.setName("Scene");

		GameObject gameObject = new GameObject();
		gameObject.setName("Objecto1");
		Transform2D trans = new Transform2D();
		gameObject.add(trans);
		scene.addChild(gameObject);

		GameObject gameObject2 = new GameObject();
		gameObject2.setName("Objecto2");
		Transform2D trans2 = new Transform2D();
		gameObject2.add(trans2);
		scene.addChild(gameObject2);

		App app = new App();
		app.Component = new ComponentAPI(app);
		JSONLoader sceneLoader = new JSONLoader(app);
		String json = sceneLoader.asJson(scene);
		Scene loaded = sceneLoader.sceneFromJson(json);
		String json2 = sceneLoader.asJson(loaded, 10);
		System.out.println(json2);
	}

	public static String readFile(String path) {
		File file = new File(path);
		try {
			FileInputStream fis = new FileInputStream(file);
			byte[] buffer = new byte[fis.available()];
			while (fis.read(buffer) > 0);
			fis.close();
			return new String(buffer);
		} catch (Exception e) {
			return null;
		}
	}

}
