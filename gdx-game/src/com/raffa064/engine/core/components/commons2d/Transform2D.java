package com.raffa064.engine.core.components.commons2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector2;
import com.raffa064.engine.core.api.InputAPI.Event;
import com.raffa064.engine.core.components.Native;
import com.raffa064.engine.core.components.commons2d.gizmo.Gizmo;

/*
	Add 2d transformations such as position, scale and rotation 
*/

public class Transform2D extends Native {
	public final Vector2 pos = new Vector2();
	public final Vector2 scale = new Vector2(1, 1);
	public float rotation;

	public final Matrix3 matrix = new Matrix3();
	public final Matrix3 worldMatrix = new Matrix3();
	
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
	
	public Transform2D getParentTransform() {
		return parentTransform;
	}

	public void calculateMatrix() {
		matrix.idt();
		matrix.translate(pos);
		matrix.scale(scale);
		matrix.rotate(rotation);

		worldMatrix.idt();
		worldMatrix.translate(pos);
		worldMatrix.scale(scale);
		worldMatrix.rotate(rotation);

		if (parentTransform != null) {
			worldMatrix.mulLeft(parentTransform.worldMatrix);
		}
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
		if (parentTransform == null) {
			parentTransform = (Transform2D) obj.parent.get("Transform2D");
		}
		
		calculateMatrix();
		
		if (Engine.editor()) {// && Engine.focusIn(obj)) {
			gizmo.render(Assets, batch);
		}
	}

	@Override
	public void exit() {
		if (Engine.editor()) {
			Input.unsubscribe(this);
		}
	}
}
