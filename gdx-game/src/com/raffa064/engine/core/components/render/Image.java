package com.raffa064.engine.core.components.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector2;
import com.raffa064.engine.core.components.Native;
import com.raffa064.engine.core.components.commons2d.Transform2D;
import com.raffa064.engine.core.api.AssetsAPI;
import com.raffa064.engine.core.components.ComponentUtils;

public class Image extends Native {
	public Color color = Color.WHITE.cpy();
	public int columns = 1;
	public int rows = 1;
	public int frame = 0;
	public boolean flipX;
	public boolean flipY;

	public Texture texture;
	public Transform2D transform;
	public TextureRegion region;

	public Image() {
		super("Image");

		exportProps(
			"texture", TEXTURE,
			"color", COLOR,
			"columns", INTEGER,
			"rows", INTEGER,
			"frame", INTEGER,
			"flipX", BOOLEAN,
			"flipY", BOOLEAN	
		);
	}

	@Override
	public void ready() {
		updateTexture();
		transform = (Transform2D) obj.get("Transform2D");
	}

	public void updateTexture() {
		if (texture == null) {
			return;
		}
		
		if (region == null) {
			region = new TextureRegion(texture);
			return;
		}

		region.setTexture(texture);
	}

	@Override
	public void process(float delta) {
		updateTexture();
		
		if (region == null) return;

		frame = frame % (columns * rows);

		int x = frame % columns;
		int y = frame / columns;
		int width = texture.getWidth() / columns;
		int height = texture.getHeight() / rows;

		region.setRegion(
			x * width, 
			y * height,
			width,
			height
		);

		region.flip(flipX, flipY);

		float halfWidth = width / 2;
		float halfHeight = height / 2;

		Matrix3 transformed = transform.transformed();

		Vector2 pos = transformed.getTranslation(new Vector2());
		Vector2 scale = transformed.getScale(new Vector2());
		float rotation = transformed.getRotation();

		Color batchColor = batch.getColor();

		if (Scene.editor()) {
			ComponentUtils.boundingBox(Assets, batch, width, height, transformed);
		}

		batch.setColor(color);
		batch.draw(
			region, 
			pos.x - halfWidth,
			pos.y - halfHeight,
			halfWidth,
			halfHeight,
			width,
			height,
			scale.x,
			scale.y,
			rotation
		);

		batch.setColor(batchColor);
	}

	@Override
	public void exit() {
	}
}
