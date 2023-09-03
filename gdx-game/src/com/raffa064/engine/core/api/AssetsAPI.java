package com.raffa064.engine.core.api;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import java.util.HashMap;
import org.json.JSONObject;
import com.raffa064.engine.core.App;
import com.raffa064.engine.core.GameObject;

public class AssetsAPI extends API {
    private HashMap<String, Object> assets = new HashMap<>();

	public AssetsAPI(App app) {
		super(app);
	}

	public Texture texture(String path) {
		if (assets.containsKey(path)) {
			return (Texture) assets.get(path);
		}

		Texture texture = new Texture(app.relative(path));
		assets.put(path, texture);

		return texture;
	}


	public String json(String path) {
		if (assets.containsKey(path)) {
			String json = (String) assets.get(path);
			return json;
		}

		String json = app.relative(path).readString();
		assets.put(path, json);

		return json;
	}
}
