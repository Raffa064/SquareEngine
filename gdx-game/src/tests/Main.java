package tests;

import com.raffa064.engine.core.App;
import com.raffa064.engine.core.GameObject;
import com.raffa064.engine.core.SceneLoader;
import com.raffa064.engine.core.api.ComponentLoader;
import com.raffa064.engine.core.components.Scene;
import com.raffa064.engine.core.components.Transform2D;
import java.io.File;
import java.io.FileInputStream;
import com.raffa064.engine.core.ScriptEngine;

public class Main {
	public static void main(String[] args) throws Exception {
		test_transpiler();
	}

	private static void test_transpiler() {
		ScriptEngine engine = new ScriptEngine();
		String script = readFile("/storage/emulated/0/AppProjects/GameEngine/gdx-game-android/assets/project/MoveComponent.js"); //"/storage/emulated/0/AppProjects/GameEngine/gdx-game/src/com/raffa064/engine/core/tests/teste.js");
		String transpile = engine.transpile(script);
		System.out.println(transpile);
		
		try {
			engine.compile(script);
			System.out.println("Sucess!");
		} catch(Exception e) {
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
		app.componentLoader = new ComponentLoader(app);
		SceneLoader sceneLoader = new SceneLoader(app);
		String json = sceneLoader.asJson(scene);
		Scene loaded = sceneLoader.fromJson(json);
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
