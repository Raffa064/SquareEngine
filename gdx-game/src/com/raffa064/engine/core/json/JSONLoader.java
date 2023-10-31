package com.raffa064.engine.core.json;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.raffa064.engine.core.App;
import com.raffa064.engine.core.Component;
import com.raffa064.engine.core.Component.ExportedProp;
import com.raffa064.engine.core.GameObject;
import com.raffa064.engine.core.Scene;
import org.json.JSONArray;
import org.json.JSONObject;
import com.badlogic.gdx.graphics.Texture;
import org.mozilla.javascript.ast.ArrayLiteral;

/*
	Used to load Object/Scene json
*/

public class JSONLoader {
	private App app;

	public JSONLoader(App app) {
		this.app = app;
	}

	public Object json(Object value, String type) throws Exception {
		switch (type) {
			case "COLOR": 
				return ((Color) value).toString(); // 12345678
			case "STRING":
			case "FLOAT":
			case "INTEGER":
			case "BOOLEAN":
				return value;
			case "VECTOR2":
				return ((Vector2) value).toString(); // (x, y)
			case "TEXTURE":
				return app.Assets.nameOf(value);
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
			case "BOOLEAN":
				return Boolean.parseBoolean(value);
			case "COLOR": 
				return Color.valueOf(value);
			case "VECTOR2":
				return parseVector2(value); // (x, y)
			case "TEXTURE":
				return app.Assets.texture(value); // (x, y)
		}

		return null;
	}

	private Component parseComponent(JSONObject componentJSON) throws Exception {
		String name = JSONUtils.getString(componentJSON, "name", "Unknown");
		Component component = (Component) app.Component.create(name);

		JSONArray exportedPropsJSON = JSONUtils.getJSONArray(componentJSON, "exportedProps", new JSONArray());
		for (int i = 0; i < exportedPropsJSON.length(); i++) {
			JSONObject propJSON = exportedPropsJSON.getJSONObject(i);

			String propName = JSONUtils.getString(propJSON, "name", "Unknown");
			String propType = JSONUtils.getString(propJSON, "type", "Unknown");
			Object propValue = parseProp(propType, propJSON.getString("value"));

			component.set(propName, propValue);
		}

		return component;
	}

	private GameObject parseGameObject(JSONObject objJSON) throws Exception {
		GameObject obj = new GameObject();

		obj.setName(JSONUtils.getString(objJSON, "name", "Unknown"));

		JSONArray components = JSONUtils.getJSONArray(objJSON, "components", new JSONArray());
		for (int i = 0; i < components.length(); i++) {
			JSONObject componentJSON = components.getJSONObject(i);
			obj.add(parseComponent(componentJSON));
		}

		JSONArray children = JSONUtils.getJSONArray(objJSON, "children", new JSONArray());
		for (int i = 0; i < children.length(); i++) {
			JSONObject child = children.getJSONObject(i);
			obj.addChild(parseGameObject(child));
		}

		return obj;
	}
	
	public GameObject objectFromJson(String json) throws Exception {
		JSONObject objJSON = new JSONObject(json);
		return parseGameObject(objJSON);
	}

	private Scene parseScene(JSONObject sceneJSON) throws Exception {
		Scene scene = new Scene();
		scene.setApp(app);

		scene.setName(JSONUtils.getString(sceneJSON, "name", "Unknown"));
		scene.orientation = JSONUtils.getString(sceneJSON, "orientation", app.configs.defaultOrientaion);
		
		JSONArray children = JSONUtils.getJSONArray(sceneJSON, "children", new JSONArray());
		for (int i = 0; i < children.length(); i++) {
			JSONObject child = children.getJSONObject(i);
			scene.addChild(parseGameObject(child));
		}

		return scene;
	}

	public Scene sceneFromJson(String json) throws Exception {
		JSONObject sceneJSON = new JSONObject(json);
		return parseScene(sceneJSON);
	}
}
