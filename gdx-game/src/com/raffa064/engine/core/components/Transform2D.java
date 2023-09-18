package com.raffa064.engine.core.components;

import com.badlogic.gdx.math.Vector2;
import com.raffa064.engine.core.Component;
import com.raffa064.engine.core.GameObject;
import com.badlogic.gdx.math.MathUtils;

public class Transform2D extends Native {
	public Vector2 pos = new Vector2();
	public Vector2 scale = new Vector2(1, 1);
	public float rotation;

	public Vector2 global_pos = new Vector2();
	public Vector2 global_scale = new Vector2();
	public float global_rotation;

	public Transform2D() {
		super("Transform2D");

		exportProps(
			"pos", VECTOR2,
			"scale", VECTOR2,
			"rotation", FLOAT
		);
	}

	public void calculateRelative() {
		global_pos.set(pos);
		global_scale.set(scale);
		global_rotation = rotation;

		Object component = obj.parent.get("Transform2D");

		if (component instanceof Transform2D) {
			Transform2D pTransfom = (Transform2D) component;
			
			float mg = pos.len();
			
			global_pos.set(
				pTransfom.global_pos.x + MathUtils.cosDeg(pTransfom.global_rotation) * mg * pTransfom.global_scale.x,
				pTransfom.global_pos.y + MathUtils.sinDeg(pTransfom.global_rotation) * mg * pTransfom.global_scale.y
			);
			
			global_scale.scl(pTransfom.global_scale);
			global_rotation += pTransfom.global_rotation;
				
		}
	}

	@Override
	public void ready() {
	}

	@Override
	public void process(float delta) {
		calculateRelative();
	}

	@Override
	public void exit() {
	}
}
