package com.raffa064.engine.core;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.raffa064.engine.core.components.Scene;
import org.json.JSONArray;
import org.json.JSONObject;
import com.raffa064.engine.core.Component.ExportedProp;

public class SceneLoader {
	private App app;

	public SceneLoader(App app) {
		this.app = app;
	}

	public Object json(Object value, String type) throws Exception {
		switch (type) {
			case "STRING":
			case "FLOAT":
			case "INTEGER":
				return value;
			case "COLOR": 
				return ((Color) value).toString(); // 12345678
			case "VECTOR2":
				return ((Vector2) value).toString(); // (x, y)
			case "GAME_OBJECT":
				throw new Exception("NOT IMPLEMENTED");
		}

		return null;
	}

	private JSONObject json(Component component, ExportedProp prop) throws Exception {
		JSONObject propJSON = new JSONObject();
		propJSON.putOpt("name", prop.name);
		propJSON.putOpt("type", prop.type);
		propJSON.putOpt("value", json(component.get(prop.name), prop.type));

		return propJSON;
	}

	private JSONObject json(Component component) throws Exception {
		JSONObject componentJSON = new JSONObject();
		componentJSON.putOpt("name", component.name);

		JSONArray exportedProps = new JSONArray();
		for (ExportedProp prop : component.exportedProps) {
			exportedProps.put(json(component, prop));
		}

		componentJSON.putOpt("exportedProps", exportedProps);

		return componentJSON;
	}

	private JSONObject json(GameObject obj) throws Exception {
		JSONObject objJSON = new JSONObject();
		objJSON.putOpt("name", obj.getName());

		JSONArray components = new JSONArray();
		for (Component component : obj.getComponents()) {
			components.put(json(component));
		}

		JSONArray children = new JSONArray();
		for (GameObject child : obj.getChildren()) {
			children.put(json(child));
		}

		objJSON.putOpt("components", components);
		objJSON.putOpt("children", children);

		return objJSON;
	}

	private JSONObject json(Scene scene) throws Exception {
		JSONObject sceneJSON = new JSONObject();
		sceneJSON.putOpt("name", scene.getName());
		// TODO: save camera state

		JSONArray childrens = new JSONArray();
		for (GameObject obj : scene.getChildren()) {
			childrens.put(json(obj));
		}

		sceneJSON.putOpt("children", childrens);

		return sceneJSON;
	}

	public String asJson(Scene scene, int indentSize) throws Exception {
		if (indentSize > 0) {
			return json(scene).toString(indentSize);
		}

		return json(scene).toString();
	}

	public String asJson(Scene scene) throws Exception {
		return asJson(scene, 0);
	}

	private Vector2 parseVector2(String value) {
		int commaIndex = value.indexOf(",");
		int parenthesesStartIndex = value.indexOf("(");
		int parenthesesEndIndex = value.indexOf(")");

		float x = Float.parseFloat(value.substring(parenthesesStartIndex + 1, commaIndex).trim());
		float y = Float.parseFloat(value.substring(commaIndex + 1, parenthesesEndIndex).trim());

		return new Vector2(x, y);
	}

	private Object parseProp(String type, String value) throws Exception {
		switch (type) {
			case "STRING":
				return value;
			case "FLOAT":
				return Float.parseFloat(value);
			case "INTEGER":
				return (int) Float.parseFloat(value); // This will prevent possible trobbles
			case "COLOR": 
				return Color.valueOf(value);
			case "VECTOR2":
				return parseVector2(value); // (x, y)
			case "GAME_OBJECT":
				throw new Exception("NOT IMPLEMENTED");
		}

		return null;
	}

	private Component parseComponent(JSONObject componentJSON) throws Exception {
		String name = componentJSON.getString("name");
		Component component = (Component) app.componentLoader.create(name);

		JSONArray exportedPropsJSON = componentJSON.getJSONArray("exportedProps");
		for (int i = 0; i < exportedPropsJSON.length(); i++) {
			JSONObject propJSON = exportedPropsJSON.getJSONObject(i);

			String propName = propJSON.getString("name");
			String propType = propJSON.getString("type");
			Object propValue = parseProp(propType, propJSON.getString("value"));

			component.set(propName, propValue);
		}

		return component;
	}

	private GameObject parseGameObject(JSONObject sceneJSON) throws Exception {
		GameObject obj = new GameObject();

		obj.setName(sceneJSON.getString("name"));

		JSONArray components = sceneJSON.getJSONArray("components");
		for (int i = 0; i < components.length(); i++) {
			JSONObject componentJSON = components.getJSONObject(i);
			obj.add(parseComponent(componentJSON));
		}

		JSONArray children = sceneJSON.getJSONArray("children");
		for (int i = 0; i < children.length(); i++) {
			JSONObject child = children.getJSONObject(i);
			obj.addChild(parseGameObject(child));
		}

		return obj;
	}

	private Scene parseScene(JSONObject sceneJSON) throws Exception {
		Scene scene = new Scene();

		scene.setName(sceneJSON.getString("name"));

		JSONArray children = sceneJSON.getJSONArray("children");
		for (int i = 0; i < children.length(); i++) {
			JSONObject child = children.getJSONObject(i);
			scene.addChild(parseGameObject(child));
		}

		return scene;
	}

	public Scene fromJson(String json) throws Exception {
		JSONObject sceneJSON = new JSONObject(json);
		return parseScene(sceneJSON);
	}
}
