package com.raffa064.engine.core.api;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.raffa064.engine.core.App;
import com.raffa064.engine.core.Component;
import com.raffa064.engine.core.GameObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public class SceneAPI extends API {
	public SceneAPI(App app) {
		super(app);
	}
	
	public GameObject prefabObject(String path) {
		try {
			String json = app.Assets.json(path);
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
}
