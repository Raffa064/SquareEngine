package com.raffa064.engine.core.components.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.MathUtils;

public class JoyStick extends SingleTouchRectUiBase {
	public Texture background;
	public Texture foreground;

	private float angle;
	private float value;
	
	public JoyStick() {
		super("JoyStick");
		
		exportProps(
			"background", TEXTURE,
			"foreground", TEXTURE
		);
	}
	
	private void updateValue() {
		Rectangle rect = getRectangle();
		Vector2 center = rect.getCenter(new Vector2());
		Vector2 pointer = getPointer();

		Vector2 distance = pointer.sub(center);

		angle = distance.angleDeg();

		float maxDistance = Math.min(rect.width, rect.height) / 2;
		value = Math.min(maxDistance, distance.len()) / maxDistance;
	}
	
	@Override
	protected boolean onTouchDown() {
		updateValue();
		return true;
	}

	@Override
	protected boolean onTouchDrag() {
		updateValue();
		return true;
	}

	@Override
	protected boolean onTouchUp() {
		angle = 0;
		value = 0;
		return true;
	}

	public float x() {
		return MathUtils.cosDeg(angle) * value;
	}

	public float y() {
		return MathUtils.sinDeg(angle) * value;
	}

	public float angle() {
		return angle;
	}
	
	public float angleRad() {
		return (angle / 180) * MathUtils.PI;
	}

	public float value() {
		return value;
	}
	
	public float value(float scalar) {
		return value * scalar;
	}

	@Override
	public void process(float delta) {
		Rectangle rect = getRectangle();

		batch.draw(background, rect.x, rect.y, rect.width, rect.height);
		batch.draw(foreground, 
			rect.x + MathUtils.cosDeg(angle) * rect.width / 2 * value,
			rect.y + MathUtils.sinDeg(angle) * rect.height / 2 * value, 
			rect.width, 
			rect.height
		);
		
		super.process(delta);
	}
}
