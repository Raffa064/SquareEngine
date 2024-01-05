package com.raffa064.engine.core.components.commons2d.gizmo.handlers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector2;
import com.raffa064.engine.core.components.commons2d.Transform2D;
import com.raffa064.engine.core.components.commons2d.gizmo.Handle;

public class RotationHandle extends Handle {
	public RotationHandle(Transform2D transform) {
		super(transform, Color.GRAY);
	}
	
    @Override
	public void onDrag(Vector2 cursor, float x, float y) {
		Matrix3 transformed = transform.worldMatrix;

		Vector2 pos = transformed.getTranslation(new Vector2());
		Vector2 distance = cursor.sub(pos);

		float parentAngle = 0;
		Transform2D parentTransform = transform.getParentTransform();
		if (parentTransform != null) {
			parentAngle = parentTransform.worldMatrix.getRotation();
		}

		transform.rotation = distance.angleDeg() - parentAngle;
	}

	@Override
	public void update() {
		float rotation = transform.worldMatrix.getRotation();
		
		pos.set(
			MathUtils.cosDeg(rotation) * 50,
			MathUtils.sinDeg(rotation) * 50
		);
	}
}
