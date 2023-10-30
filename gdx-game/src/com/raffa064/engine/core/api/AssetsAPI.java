package com.raffa064.engine.core.api;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.raffa064.engine.core.App;
import com.raffa064.engine.core.collision.Shape;
import com.raffa064.engine.core.json.JSONUtils;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.badlogic.gdx.graphics.Pixmap;

public class AssetsAPI extends API {
    private HashMap<String, Object> assets = new HashMap<>();

	public AssetsAPI(App app) {
		super(app);
	}

	@Override
	public APIState createState() {
		return buildState();
	}


	@Override
	public void useState(APIState values) {
	}
	
	public String nameOf(Object asset) {
		for (Map.Entry<String, Object> entry : assets.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			
			if (asset == value) {
				return key;
			}
		}
		
		return null;
	}
	
	public Texture placeholder(Color color) {
		return placeholder(color.toString());
	}
	
	public Texture placeholder(String color) {
		if (color.startsWith("#")) {
			color = color.substring(1);
		}
		
		if (assets.containsKey(color)) {
			return (Texture) assets.get(color);
		}
		
		Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
		Color colorObj = Color.valueOf(color);
		pixmap.drawPixel(0, 0, Color.rgba8888(colorObj));
		Texture texture = new Texture(pixmap);
		
		assets.put(color, texture);
		
		return texture;
	}

	public Texture texture(String path) {
		if (assets.containsKey(path)) {
			return (Texture) assets.get(path);
		}

		Texture texture = new Texture(app.path(path));
		assets.put(path, texture);

		return texture;
	}

	public Shape shape(String path) {
		if (assets.containsKey(path)) {
			return (Shape) assets.get(path);
		}

		String json = readFile(path);

		try {
			JSONArray array = new JSONArray(json);
			float[] shape = new float[array.length()];

			for (int i = 0; i < shape.length; i++) {
				shape[i] = array.get(i);
			}

			Shape shapeObj = new Shape(shape);
			assets.put(path, shape);

			return shapeObj;
		} catch (JSONException e) {
			return null;
		}
	}

	public String readFile(String path) {
		if (assets.containsKey(path)) {
			String content = (String) assets.get(path);
			return content;
		}

		String content = app.path(path).readString();
		assets.put(path, content);

		return content;
	}

	public BitmapFont font(String path) {
		try {
			JSONObject json = new JSONObject(readFile(path)); // Certifique-se de que 'jsonString' contenha seu JSON.

			FreeTypeFontGenerator generator = fontGenerator(json.getString("font"));
			FreeTypeFontParameter params = new FreeTypeFontParameter();
			
			params.size = JSONUtils.getInt(json, "size", 10);
			params.mono = JSONUtils.getBoolean(json, "mono", false);
			params.color = Color.valueOf(JSONUtils.getString(json, "color", "ffffff"));
			params.gamma = (float) JSONUtils.getDouble(json, "gamma", 1.8);
			params.renderCount = JSONUtils.getInt(json, "renderCount", 2);
			params.borderWidth = (float) JSONUtils.getDouble(json, "borderWidth", 0);
			params.borderColor = Color.valueOf(JSONUtils.getString(json, "borderColor", "000000"));
			params.borderStraight = JSONUtils.getBoolean(json, "borderStraight", false);
			params.borderGamma = (float) JSONUtils.getDouble(json, "borderGamma", 1.8);
			params.shadowOffsetX = JSONUtils.getInt(json, "shadowOffsetX", 0);
			params.shadowOffsetY = JSONUtils.getInt(json, "shadowOffsetY", 0);
			params.shadowColor = Color.valueOf(JSONUtils.getString(json, "shadowColor", "00000010"));
			params.spaceX = JSONUtils.getInt(json, "spaceX", 0);
			params.spaceY = JSONUtils.getInt(json, "spaceY", 0);
			params.padTop = JSONUtils.getInt(json, "padTop", 0);
			params.padLeft = JSONUtils.getInt(json, "padLeft", 0);
			params.padBottom = JSONUtils.getInt(json, "padBottom", 0);
			params.padRight = JSONUtils.getInt(json, "padRight", 0);
			params.characters = JSONUtils.getString(json, "characters", FreeTypeFontGenerator.DEFAULT_CHARS);
			params.kerning = JSONUtils.getBoolean(json, "kerning", true);
			params.flip = JSONUtils.getBoolean(json, "flip", false);
			params.genMipMaps = JSONUtils.getBoolean(json, "genMipMaps", false);
			params.minFilter = Texture.TextureFilter.valueOf(JSONUtils.getString(json, "minFilter", "Nearest"));
			params.magFilter = Texture.TextureFilter.valueOf(JSONUtils.getString(json, "magFilter", "Nearest"));
			params.incremental = JSONUtils.getBoolean(json, "incremental", false);
			
			BitmapFont font = generator.generateFont(params);
			int randID = (int) Math.floor((Math.random() * Integer.MAX_VALUE));
			assets.put(path + randID, font);
			return font;
		} catch (Exception e) {
			return null;
		}
	}

	private FreeTypeFontGenerator fontGenerator(String path) {
		if (assets.containsKey(path)) {
			return (FreeTypeFontGenerator) assets.get(path);
		}

		FileHandle relative = app.path(path);

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
