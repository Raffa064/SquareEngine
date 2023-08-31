package com.raffa064.engine.core.components;

import com.badlogic.gdx.math.Vector2;
import com.raffa064.engine.core.Component;

public class Transform2D extends Native {
	public Vector2 pos = new Vector2();
	public Vector2 scale = new Vector2(1, 1);
	public float rotation;

	public Transform2D() {
		super("Transform2D");
		
		exportProps(
			"pos", VECTOR2,
			"scale", VECTOR2,
			"rotation", FLOAT
		);
	}
	
	@Override
	public void ready() {
	}

	@Override
	public void process(float delta) {
	}

	@Override
	public void exit() {
	}
}
