package com.raffa064.engine.core.components.commons2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector2;
import com.raffa064.engine.core.components.Native;
import com.badlogic.gdx.math.MathUtils;

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
		if (Engine.editor() && Engine.focusIn(obj)) {
			Matrix3 transformed = transformed();

			Vector2 pos = transformed.getTranslation(new Vector2());
			float rotation = transformed.getRotation();

			Texture handle = Assets.texture(Gdx.files.internal("gizmo/handle.png"));

			batch.setColor(Color.BLUE);
			batch.draw(handle, pos.x - 15, pos.y - 15, 30, 30);

			batch.setColor(Color.RED);
			batch.draw(handle, pos.x + 100 - 15, pos.y - 15, 30, 30);

			batch.setColor(Color.GREEN);
			batch.draw(handle, pos.x - 15, pos.y + 100 - 15, 30, 30);

			batch.setColor(Color.GRAY);
			batch.draw(handle, pos.x + MathUtils.cosDeg(rotation) * 50 - 15, pos.y  + MathUtils.sinDeg(rotation) * 50 - 15, 30, 30);
		}
	}

	@Override
	public void exit() {
	}
}
