package com.raffa064.engine.core.components.commons2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector2;
import com.raffa064.engine.core.components.Native;
import com.badlogic.gdx.math.MathUtils;
import com.raffa064.engine.core.api.InputAPI.*;
import com.raffa064.engine.core.api.AssetsAPI;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Affine2;

/*
	Add 2d transformations such as position, scale and rotation 
*/

public class Transform2D extends Native {
	public final Vector2 pos = new Vector2();
	public final Vector2 scale = new Vector2(1, 1);
	public float rotation;
	
	private Transform2D parentTransform;
	private Gizmo gizmo;
	
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
		
		if (Engine.editor()) {
			Input.subscribe(this);
			
			Texture handlerTexture = Assets.texture(Gdx.files.internal("gizmo/handle.png"));
			gizmo = new Gizmo(this, handlerTexture);
		}
	}

	@Override
	public boolean input(Event event) {
		if (Engine.editor()) {
			if (event.type == Input.TOUCH_EVENT) {
				if (gizmo.input(event, Scene.getCamera())) {
					Engine.requestFocus(obj);
					return true;
				} else {
					if (Engine.focusIn(obj)) {
						Engine.requestFocus(null);
					}
				}
			}
		}
		
		return false;
	}
	
	@Override
	public void process(float delta) {
		if (Engine.editor()) { //&& Engine.focusIn(obj)) {
			gizmo.render(Assets, batch);
		}
	}

	@Override
	public void exit() {
		if (Engine.editor()) {
			Input.unsubscribe(this);
		}
	}
	
	private static class Gizmo {
		private Transform2D transform;
		private Texture handleTexture;
		private Handle motionHandle;
		private Handle rotationHandle;
		private Handle scaleXHandle;
		private Handle scaleYHandle;
		private Handle[] handlers;

		public Gizmo(Transform2D transform, Texture handleTexture) {
			this.transform = transform;
			this.handleTexture = handleTexture;
			
			initializeHandlers();
		}

		public void initializeHandlers() {
			motionHandle = new Handle(transform, Color.BLUE) {
				@Override
				public void onDrag(Vector2 cursor, float x, float y) {
					Matrix3 parent = null;
					
					if (transform.parentTransform != null) {
						parent = transform.parentTransform.transformed();
					} else {
						parent = new Matrix3();
					}
					
					parent = parent.inv();
					
					Affine2 affine2 = new Affine2();
					affine2.scale(parent.getScale(new Vector2()));
					affine2.rotate(parent.getRotation());
					
					Vector2 motion = new Vector2(x, y);
					affine2.applyTo(motion);
					
					transform.pos.add(motion);
				}
			};
			
			rotationHandle = new Handle(transform, Color.GRAY) {
				@Override
				public void onDrag(Vector2 cursor, float x, float y) {
					Matrix3 transformed = transform.transformed();

					Vector2 pos = transformed.getTranslation(new Vector2());
					Vector2 distance = cursor.sub(pos);
					
					float parentAngle = 0;
					Transform2D parentTransform = transform.parentTransform;
					if (parentTransform != null) {
						parentAngle = parentTransform.transformed().getRotation();
					}
					
					transform.rotation = distance.angleDeg() - parentAngle;
				}
			};
			
			handlers = new Handle[]{ motionHandle, rotationHandle };
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
			Matrix3 transformed = transform.transformed();
			Vector2 pos = transformed.getTranslation(new Vector2());
			float rotation = transformed.getRotation();
			
			// Update rotation handle position
			rotationHandle.relativePos.set(
				MathUtils.cosDeg(rotation) * 70,
				MathUtils.sinDeg(rotation) * 70
			);

			for (Handle handle : handlers) {
				float radius = handle.radius;
				
				batch.setColor(handle.color);
				batch.draw(
					handleTexture,
					pos.x - radius + handle.relativePos.x,
					pos.y - radius + handle.relativePos.y,
					radius * 2,
					radius * 2
				);
			}
		}
	}
	
	private static abstract class Handle {
		private Transform2D transform;
		private Color color;
		private Vector2 relativePos = new Vector2();
		private float radius = 15;
		private int activedPointer = -1;
		private Vector2 dragOffset = new Vector2();

		public Handle(Transform2D transform, Color color) {
			this.transform = transform;
			this.color = color;
		}
		
		public boolean down(Vector2 cursor, int pointer) {
			if (activedPointer < 0) {
				Matrix3 transformed = transform.transformed();

				Vector2 pos = transformed.getTranslation(new Vector2());
				pos.add(relativePos);

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
	}
}
