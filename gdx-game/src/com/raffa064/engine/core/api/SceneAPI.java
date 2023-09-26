package com.raffa064.engine.core.api;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.raffa064.engine.core.App;
import com.raffa064.engine.core.Component;
import com.raffa064.engine.core.GameObject;
import com.raffa064.engine.core.Scene;
import java.util.List;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import java.util.ArrayList;

public class SceneAPI extends API {
	public HistoryList<Scene> history = new HistoryList<>(); // Populated by App.setScene()
	
	public SceneAPI(App app) {
		super(app);
	}
	
	@Override
	public APIState createState() {
		return buildState();
	}

	@Override
	public void useState(APIState values) {
	}
	
	public void setScene(String sceneName) throws Exception {
		Scene scene = app.loadScene(sceneName);
		app.setScene(scene, true);
	}

	public boolean backScene() {
		Scene back = history.back();
		
		if (back != null) {
			app.setScene(back, true);
			return true;
		}
		
		return false;
	}
	
	public boolean forwardScene() {
		Scene forward = history.forward();

		if (forward != null) {
			app.setScene(forward, true);
			return true;
		}

		return false;
	}
	
	public GameObject prefabObject(String path) {
		try {
			String json = app.Assets.readFile(path);
			GameObject obj = app.jsonLoader.objectFromJson(json);
			return obj;
		} catch (Exception e) {
			return null;
		}
	}
	
	public GameObject createObject(String name, Object... components) {
		GameObject obj = new GameObject();
		obj.setName(name);
		
		for (Object component : components) {
			if (component instanceof Scriptable) {
				Object comp = ScriptableObject.getProperty((Scriptable) component, "_javaInstance");
				obj.add((Component) comp);
				continue;
			}
			

			obj.add((Component) component);
		}
		
		return obj;
	}
	
	public void addToScene(GameObject obj) {
		app.currentScene.addChild(obj);
	}
	
	public OrthographicCamera getCamera() {
		return app.currentScene.camera;
	}

	public float width() {
		return app.currentScene.camera.viewportWidth;
	}

	public float height() {
		return app.currentScene.camera.viewportHeight;
	}
	
	public float viewportWidth() {
		return app.viewportWidth;
	}

	public float viewportHeight() {
		return app.viewportHeight;
	}
	
	public Color getBackground() {
		return app.currentScene.backgroundColor;
	}

	public void setBackground(float r, float g, float b, float a) {
		app.currentScene.backgroundColor.set(r, g, b, a);
	}

	public void setBackground(float r, float g, float b) {
		app.currentScene.backgroundColor.set(r, g, b, 1);
	}
	
	public void setBackground(Color color) {
		app.currentScene.backgroundColor.set(color);
	}

	public void setBackground(String hex) {
		app.currentScene.backgroundColor.set(Color.valueOf(hex));
	}

	public void setBackground(float gray) {
		app.currentScene.backgroundColor.set(gray, gray, gray, 1);
	}
	
	public int fps() {
		return Gdx.graphics.getFramesPerSecond();
	}
	
	public GameObject findObject(String name, GameObject from) {
		for (GameObject obj : from.getChildren()) {
			if (obj.getName().equals(name)) {
				return obj;
			}
			
			GameObject _obj = findObject(name, obj);
			
			if (_obj != null) {
				return _obj;
			}
		}
		
		return null;
	}
	
	public GameObject findObject(String name) {
		return findObject(name, app.currentScene);
	}
	
	public static class HistoryList<T> {
		public List<T> history = new ArrayList<>();
		public int index;
		
		public void add(T obj) {
			if (history.contains(obj)) return;
			
			if (index == history.size() - 1 || history.isEmpty()) {
				history.add(obj);
				index = history.size() - 1;
			}
			
			while (index != history.size() - 1) {
				history.remove(history.size()-1);
			}
		}

		public T back() {
			if (index > 0) {
				return history.get(--index);	
			}
			
			return null;
		}

		public T forward() {
			if (index < history.size() - 1) {
				return history.get(++index);	
			}

			return null;
		}
	}
	
	public String tree(GameObject obj, String log, int level, int[] components) {
		for (int i = 0; i < level; i++) {
			log += ' ';
		}

		log += obj.getName() + '\n';
		components[0] += obj.getComponents().size();

		List<GameObject> children = obj.getChildren();
		for (int i = 0; i < children.size(); i++) {
			GameObject child = children.get(i);
			log = tree(child, log, level+1, components);
		}

		if (level == 1) {
			log += "Component Amount: " + components[1];
		}

		return log;
	}
	
	public String tree(GameObject obj) {
		return tree(obj, "", 1, new int[2]);
	}
	
	public String tree() {
		return tree(app.currentScene);
	}
}
