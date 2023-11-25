package com.raffa064.engine.core.components.ui;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.raffa064.engine.core.api.InputAPI;

public abstract class SingleTouchRectUiBase extends RectUIBase {
	public int activedPointer = -1;
	private Vector2 pointer = new Vector2();
	private boolean down, pressed, up;

	public SingleTouchRectUiBase(String name) {
		super(name);
	}
	
	@Override
	public boolean input(InputAPI.Event event) {
		if (Engine.editor()) return false;
		
		Rectangle rect = getRectangle();
		switch (event.type) { 
			case Input.TOUCH_EVENT:
				Vector2 cursor = event.getWorldTouch(Scene.getCamera());
				if (event.down && activedPointer < 0 && rect.contains(cursor)) {
					activedPointer = event.pointer;
					down = true;
					pressed = true;
					return onTouchDown();
				}
				
				if (event.drag && activedPointer == event.pointer) {
					return onTouchDrag();
				}

				if (event.up && activedPointer == event.pointer) {
					activedPointer = -1;
					pressed = false;
					up = true;
					return onTouchUp();
				}

				break;
		}

		return false;
	}
	
	public boolean isPointerLocked() {
		return activedPointer >= 0;
	}
	
	public Vector2 getPointer() {
		if (activedPointer < 0) return null;
		
		pointer.set(Input.x(activedPointer), Input.y(activedPointer));
		return pointer;
	}

	protected abstract boolean onTouchDown();
	
	protected abstract boolean onTouchDrag();
	
	protected abstract boolean onTouchUp();
	
	public boolean down() {
		return down;
	}
	
	public boolean pressed() {
		return pressed;
	}
	
	public boolean up() {
		return up;
	}

	@Override
	public void process(float delta) {
		super.process(delta); // render in-editor bounding box
		
		down = false;
		up = false;
	}
}
