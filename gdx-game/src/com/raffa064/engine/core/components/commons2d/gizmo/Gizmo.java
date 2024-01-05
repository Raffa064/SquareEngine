package com.raffa064.engine.core.components.commons2d.gizmo;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector2;
import com.raffa064.engine.core.api.AssetsAPI;
import com.raffa064.engine.core.components.commons2d.Transform2D;
import com.raffa064.engine.core.api.InputAPI.*;
import com.raffa064.engine.core.components.commons2d.gizmo.handlers.MotionHandle;
import com.raffa064.engine.core.components.commons2d.gizmo.handlers.RotationHandle;
import com.raffa064.engine.core.components.commons2d.gizmo.handlers.ResizerHandle;

/*
	Gizmo (used to translate, rotate and scale objects)
	This class is used to render gizmo on screen
*/

public class Gizmo {
	private Transform2D transform;
	private Texture handleTexture;
	private Handle motionHandle;
	private Handle rotationHandle;
	private Handle resizeXHandle;
	private Handle resizeYHandle;
	private Handle[] handlers;

	public Gizmo(Transform2D transform, Texture handleTexture) {
		this.transform = transform;
		this.handleTexture = handleTexture;

		initializeHandlers();
	}

	public void initializeHandlers() {
		motionHandle = new MotionHandle(transform);		
		rotationHandle = new RotationHandle(transform);
		resizeXHandle = new ResizerHandle(transform, new Vector2(1, 0));
		resizeYHandle = new ResizerHandle(transform, new Vector2(0, 1));
		
		handlers = new Handle[]{ motionHandle, rotationHandle, resizeXHandle, resizeYHandle };
	}

	public boolean input(Event event, OrthographicCamera cam) {
		Vector2 cursor = event.getWorldTouch(cam);
		int pointer = event.pointer;

		for (Handle handle : handlers) {
			if (event.down) {
				if (handle.down(cursor, pointer)) return true;
			}

			if (event.drag) {
				if (handle.drag(cursor, pointer)) return true;
			}

			if (event.up) {
				if (handle.up(cursor, pointer)) return true;
			}
		}

		return false;
	}

	public void render(AssetsAPI Assets, SpriteBatch batch) {
		// Get current transformations
		Matrix3 transformed = transform.worldMatrix;
		Vector2 pos = transformed.getTranslation(new Vector2());

		Color batchColor = batch.getColor();
		Color color = new Color();
		
		for (Handle handle : handlers) {
			handle.update();
			
			color.set(handle.color);
			float radius = handle.radius;
			Vector2 handlePos = handle.pos;
			
			// Change opacity when actived
			color.a = handle.activedPointer < 0 ? 1 : .5f;
			
			batch.setColor(color);
			batch.draw(
				handleTexture,
				pos.x - radius + handlePos.x,
				pos.y - radius + handlePos.y,
				radius * 2,
				radius * 2
			);
		}

		batch.setColor(batchColor);
	}
}
