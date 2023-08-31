package com.raffa064.engine.core.components;

import com.badlogic.gdx.graphics.Texture;
import com.raffa064.engine.core.Component;
import com.raffa064.engine.core.api.Assets;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class Image extends Native {
	public String texturePath;
	public Texture texture;
	
	public Transform2D transform;
	
	public Image() {
		super("Image");
	}

	@Override
	public void ready() {
		if (texture == null) {
			texture = Assets.texture(texturePath);
		}
		
		transform = (Transform2D) obj.get("Transform2D");
	}

	@Override
	public void process(float delta) {
		texture = Assets.texture(texturePath);
		
		Sprite sprite = new Sprite(texture);
		
		batch.draw(
			sprite, 
			transform.pos.x - texture.getWidth() / 2f,
			transform.pos.y - texture.getHeight() / 2f,
			texture.getWidth() / 2f,
			texture.getHeight() / 2f,
			texture.getWidth(),
			texture.getHeight(),
			transform.scale.x,
			transform.scale.y,
			transform.rotation
		);
	}

	@Override
	public void exit() {
	}
}
