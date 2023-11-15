package com.raffa064.engine.core.components.commons2d;

import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector2;
import com.raffa064.engine.core.components.Native;
import com.badlogic.gdx.graphics.Texture;

/*
	Add 2d transformations such as position, scale and rotation 
*/

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
			mat.mulLeft(parentTransform.transformed());
		} 
		
		return mat;
	}

	@Override
	public void ready() {
		parentTransform = (Transform2D) obj.parent.get("Transform2D");
	}

	@Override
	public void process(float delta) {
		if (Input.keyPressed(Input.D)) {
			Texture yes = Assets.placeholder("0000ffff", 10);
			Texture no = Assets.placeholder("ff0000ff", 10);

			Vector2 pos = transformed().getTranslation(new Vector2());

			batch.draw(parentTransform == null ? no : yes, pos.x - 5, pos.y - 5);
		}
	}

	@Override
	public void exit() {
	}
}
