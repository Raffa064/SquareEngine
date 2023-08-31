package com.raffa064.engine.core.api;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import java.util.HashMap;
import org.json.JSONObject;

public class Assets {
    private static HashMap<String, Object> assets = new HashMap<>();

	public static Texture texture(String path) {
		if (assets.containsKey(path)) {
			return (Texture) assets.get(path);
		}

		Texture texture = new Texture(path);
		assets.put(path, texture);

		return texture;
	}
	
}
