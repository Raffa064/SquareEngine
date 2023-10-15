package com.raffa064.engine.core.components.commons2d;

import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector2;
import com.raffa064.engine.core.components.Native;

public class Transform2D extends Native {
	public final Vector2 pos = new Vector2();
	public final Vector2 scale = new Vector2(1, 1);
	public float rotation;
	
	private Transform2D parentTransform;

	public Transform2D() {
		super("Transform2D");

		exportProps(
			"pos", VECTOR2,
			"scale", VECTOR2,
			"rotation", FLOAT
		);
	}
	
	public Matrix3 transformed() {
		Matrix3 mat = new Matrix3();

		mat.translate(pos);
		mat.scale(scale);
		mat.rotate(rotation);
		
		if (parentTransform != null) {
			mat.mul(parentTransform.transformed());
		} 
		
		return mat;
	}

	@Override
	public void ready() {
		parentTransform = (Transform2D) obj.parent.get("Transform2D");
	}

	@Override
	public void process(float delta) {
	}

	@Override
	public void exit() {
	}
}
