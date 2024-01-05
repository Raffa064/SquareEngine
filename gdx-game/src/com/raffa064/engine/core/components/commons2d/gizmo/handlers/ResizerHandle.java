package com.raffa064.engine.core.components.commons2d.gizmo.handlers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.raffa064.engine.core.components.commons2d.Transform2D;
import com.raffa064.engine.core.components.commons2d.gizmo.Handle;

public class ResizerHandle extends Handle {
	public Vector2 axis;
	
	public ResizerHandle(Transform2D transform, Vector2 axis) {
		super(transform, axis.x == 1? Color.RED : Color.GREEN);
		
		this.axis = axis;
	}
	
    @Override
	public void onDrag(Vector2 cursor, float x, float y) {
		Vector2 scale = screnToObject(x, y);

		scale.scl(axis);
		scale.scl(0.001f);

		transform.scale.add(scale);
	}

	@Override
	public void update() {
		float rotation = transform.worldMatrix.getRotation();

		if (axis.x == 1) {
			// X axis
			pos.set(
				MathUtils.cosDeg(rotation) * 100,
				MathUtils.sinDeg(rotation) * 100
			);
			
			return;
		}
		
		// Y axis
		pos.set(
			MathUtils.cosDeg(rotation + 90) * 100,
			MathUtils.sinDeg(rotation + 90) * 100
		);
	}
}
