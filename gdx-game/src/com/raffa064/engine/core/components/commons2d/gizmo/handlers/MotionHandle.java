package com.raffa064.engine.core.components.commons2d.gizmo.handlers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.raffa064.engine.core.components.commons2d.Transform2D;
import com.raffa064.engine.core.components.commons2d.gizmo.Handle;

public class MotionHandle extends Handle {
	public MotionHandle(Transform2D transform) {
		super(transform, new Color(0, .8f, 1, 1));
	}

	@Override
	public void onDrag(Vector2 cursor, float x, float y) {
		Vector2 motion = screnToObject(x, y);

		// Add motion vector to position vector
		transform.pos.add(motion);
	}

	@Override
	public void update() {
		pos.set(0, 0); // Always in center
	}
}
