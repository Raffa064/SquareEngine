package com.raffa064.engine.core.components.commons2d.triggers;

import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.raffa064.engine.core.components.ComponentUtils;
import com.raffa064.engine.core.components.commons2d.Transform2D;
import com.raffa064.engine.core.components.commons2d.Trigger;

public class RectTrigger extends Trigger {
	private Transform2D transform;

	public float width;
	public float height;
	
	public RectTrigger() {
		super("RectTrigger");
	}

	@Override
	public void ready() {
		transform = (Transform2D) obj.get("Transform2D");

		layers.add("default");
		masks.add("default");
		super.ready();
	}
	
	public Rectangle getTransformedRect() {
		Matrix3 transformed = transform.transformed();
		Vector2 pos = transformed.getTranslation(new Vector2());
		Vector2 scale = transformed.getScale(new Vector2());

		float scaledWidth = this.width * scale.x;
		float scaledHeight = this.height * scale.y;

		Rectangle rect = new Rectangle(pos.x - scaledWidth / 2, pos.y - scaledHeight / 2, scaledWidth, scaledHeight);

		return rect;
	}

	@Override
	public void process(float delta) {
		if (Engine.editor() && Engine.focusIn(obj)) {
			ComponentUtils.boundingBox(Assets, batch, width, height, transform.transformed(), false);
		}
	}

	@Override
	public boolean overlap(Trigger trigger) {
		if (trigger instanceof RectTrigger) {
			RectTrigger rTrigger = (RectTrigger) trigger;
			
			Rectangle a = getTransformedRect();
			Rectangle b = rTrigger.getTransformedRect();
			
			return a.overlaps(b);
		}
		
		return false;
	}
}
