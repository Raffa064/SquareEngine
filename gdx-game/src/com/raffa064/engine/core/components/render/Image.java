package com.raffa064.engine.core.components.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector2;
import com.raffa064.engine.core.components.Native;
import com.raffa064.engine.core.components.commons2d.Transform2D;

public class Image extends Native {
	public String texturePath;
	public Color color = Color.WHITE.cpy();
	public int columns = 1;
	public int rows = 1;
	public int frame = 0;
	public boolean flipX;
	public boolean flipY;

	public Texture texture;
	public String currentTexture;
	public Transform2D transform;
	public TextureRegion region;
	
	public Image() {
		super("Image");
		
		exportProps(
			"texturePath", STRING,
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
		setTexture();
		transform = (Transform2D) obj.get("Transform2D");
	}
	
	public void setTexture() {
		currentTexture = texturePath;
		texture = Assets.texture(currentTexture);
		
		if (region == null) {
			region = new TextureRegion(texture);
			return;
		}
		
		region.setTexture(texture);
	}

	@Override
	public void process(float delta) {
		if (texturePath == null) {
			return;
		}
		
		if (!texturePath.equals(currentTexture) || texture == null) {
			setTexture();
		}
		
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
		
		Color batchColor = batch.getColor();
		
		Matrix3 transformed = transform.transformed();
		
		Vector2 pos = transformed.getTranslation(new Vector2());
		Vector2 scale = transformed.getScale(new Vector2());
		float rotation = transformed.getRotation();
		
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
