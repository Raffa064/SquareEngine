package com.raffa064.engine.core.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Image extends Native {
	public String texturePath;
	public Color color = Color.WHITE.cpy();
	public int columns = 1;
	public int rows = 1;
	public int frame = 0;

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
			"frame", INTEGER
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

		float halfWidth = width / 2;
		float halfHeight = height / 2;
		
		Color batchColor = batch.getColor();
		
		batch.setColor(color);
		batch.draw(
			region, 
			transform.global_pos.x - halfWidth,
			transform.global_pos.y - halfHeight,
			halfWidth,
			halfHeight,
			width,
			height,
			transform.global_scale.x,
			transform.global_scale.y,
			transform.global_rotation
		);
		
		batch.setColor(batchColor);
	}

	@Override
	public void exit() {
	}
}
