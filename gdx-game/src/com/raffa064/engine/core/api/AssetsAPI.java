package com.raffa064.engine.core.api;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.raffa064.engine.core.App;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

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

	public String readFile(String path) {
		if (assets.containsKey(path)) {
			String content = (String) assets.get(path);
			return content;
		}

		String content = app.relative(path).readString();
		assets.put(path, content);

		return content;
	}
	
	public BitmapFont font(String path) {
		try {
			JSONObject json = new JSONObject(readFile(path)); // Certifique-se de que 'jsonString' contenha seu JSON.

			FreeTypeFontGenerator generator = fontGenerator(json.getString("font"));
			FreeTypeFontParameter params = new FreeTypeFontParameter();

			if (json.has("size")) {
				params.size = json.getInt("size");
			}
			
			if (json.has("mono")) {
				params.mono = json.getBoolean("mono");
			}
			
			if (json.has("color")) {
				params.color = Color.valueOf(json.getString("color"));
			}
			
			if (json.has("gamma")) {
				params.gamma = (float) json.getDouble("gamma");
			}
			
			if (json.has("renderCount")) {
				params.renderCount = json.getInt("renderCount");
			}
			
			if (json.has("borderWidth")) {
				params.borderWidth = (float) json.getDouble("borderWidth");
			}
			
			if (json.has("borderColor")) {
				params.borderColor = Color.valueOf(json.getString("borderColor"));
			}
			
			if (json.has("borderStraight")) {
				params.borderStraight = json.getBoolean("borderStraight");
			}
			
			if (json.has("borderGamma")) {
				params.borderGamma = (float) json.getDouble("borderGamma");
			}
			
			if (json.has("shadowOffsetX")) {
				params.shadowOffsetX = json.getInt("shadowOffsetX");
			}
			
			if (json.has("shadowOffsetY")) {
				params.shadowOffsetY = json.getInt("shadowOffsetY");
			}
			
			if (json.has("shadowColor")) {
				params.shadowColor = Color.valueOf(json.getString("shadowColor"));
			}
			
			if (json.has("spaceX")) {
				params.spaceX = json.getInt("spaceX");
			}
			
			if (json.has("spaceY")) {
				params.spaceY = json.getInt("spaceY");
			}
			
			if (json.has("padTop")) {
				params.padTop = json.getInt("padTop");
			}
			
			if (json.has("padLeft")) {
				params.padLeft = json.getInt("padLeft");
			}
			
			if (json.has("padBottom")) {
				params.padBottom = json.getInt("padBottom");
			}
			
			if (json.has("padRight")) {
				params.padRight = json.getInt("padRight");
			}
			
			if (json.has("characters")) {
				params.characters = json.getString("characters");
			}
			
			if (json.has("kerning")) {
				params.kerning = json.getBoolean("kerning");
			}
			
			if (json.has("flip")) {
				params.flip = json.getBoolean("flip");
			}
			
			if (json.has("genMipMaps")) {
				params.genMipMaps = json.getBoolean("genMipMaps");
			}
			
			if (json.has("minFilter")) {
				params.minFilter = Texture.TextureFilter.valueOf(json.getString("minFilter"));
			}
			
			if (json.has("magFilter")) {
				params.magFilter = Texture.TextureFilter.valueOf(json.getString("magFilter"));
			}
			
			BitmapFont font = generator.generateFont(params);
			int randID = (int) Math.floor((Math.random() * Integer.MAX_VALUE));
			assets.put(path + randID, font);
			return font;
		} catch (JSONException e) {
			return null;
		}
	}
	
	private FreeTypeFontGenerator fontGenerator(String path) {
		if (assets.containsKey(path)) {
			return (FreeTypeFontGenerator) assets.get(path);
		}

		FileHandle relative = app.relative(path);

		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(relative);

		assets.put(path, generator);

		return generator;
	}
	
	public void dispose() {
		for (Map.Entry<String, Object> entry : assets.entrySet()) {
			Object value = entry.getValue();

			if (value instanceof Texture) {
				((Texture)value).dispose();
			}
			
			if (value instanceof FreeTypeFontGenerator) {
				((FreeTypeFontGenerator)value).dispose();
			}
			
			if (value instanceof BitmapFont) {
				((BitmapFont)value).dispose();
			}
		}
		
		assets.clear();
	}
}
