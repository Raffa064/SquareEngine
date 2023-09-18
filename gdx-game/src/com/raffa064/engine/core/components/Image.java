package com.raffa064.engine.core.components;

import com.badlogic.gdx.graphics.Texture;
import com.raffa064.engine.core.Component;
import com.raffa064.engine.core.api.AssetsAPI;
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
			transform.global_pos.x - texture.getWidth() / 2f,
			transform.global_pos.y - texture.getHeight() / 2f,
			texture.getWidth() / 2f,
			texture.getHeight() / 2f,
			texture.getWidth(),
			texture.getHeight(),
			transform.global_scale.x,
			transform.global_scale.y,
			transform.global_rotation
		);
	}

	@Override
	public void exit() {
	}
}
