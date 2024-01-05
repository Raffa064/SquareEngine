package com.raffa064.engine.core.components.commons2d.gizmo;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector2;
import com.raffa064.engine.core.components.commons2d.Transform2D;

/*
	Interactive gizmo handlers (used to translate, rotate and scale objects)
	This class is used to store handler data and handle input events
*/

public abstract class Handle {
	public Transform2D transform;
	public Color color;
	public Vector2 pos = new Vector2();
	public float radius = 18;
	public int activedPointer = -1;
	public Vector2 dragOffset = new Vector2();

	public Handle(Transform2D transform, Color color) {
		this.transform = transform;
		this.color = color;
	}
	
	public Vector2 screnToObject(float screenX, float screenY) {
		Matrix3 matrix = null;

		// Get (inverse) parent transformation matrix
		if (transform.getParentTransform() != null) {
			Transform2D parentTransform = transform.getParentTransform();
			Matrix3 worldMatrix = parentTransform.worldMatrix;

			matrix = new Matrix3(worldMatrix).inv();
		} else {
			matrix = new Matrix3().inv();
		}

		Vector2 tmp = new Vector2();

		// Create affine2 with scale and rotation (translation will not be used)
		Affine2 affine2 = new Affine2();
		affine2.rotate(matrix.getRotation());
		affine2.scale(matrix.getScale(tmp));

		// Apply matrix to the motion vector
		tmp.set(screenX, screenY);
		affine2.applyTo(tmp);

		return tmp;
	}

	public boolean down(Vector2 cursor, int pointer) {
		if (activedPointer < 0) {
			Matrix3 transformed = transform.worldMatrix;

			Vector2 pos = transformed.getTranslation(new Vector2());
			pos.add(this.pos);

			float distance = (float) Math.sqrt(Math.pow(pos.x - cursor.x, 2) + Math.pow(pos.y - cursor.y, 2));

			if (distance < radius) {
				activedPointer = pointer;
				dragOffset.set(cursor);
				return true;
			}
		}

		return false;
	}

	public boolean drag(Vector2 cursor, int pointer) {
		if (pointer == activedPointer) {
			onDrag(cursor, cursor.x - dragOffset.x, cursor.y - dragOffset.y);
			dragOffset.set(cursor);

			return true;
		}

		return false;
	}

	public boolean up(Vector2 cursor, int pointer) {
		if (pointer == activedPointer) {
			activedPointer = -1;
			return true;
		}

		return false;
	}

	public abstract void onDrag(Vector2 cursor, float x, float y);
	
	public abstract void update();
}
