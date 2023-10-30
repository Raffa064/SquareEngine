package com.raffa064.engine.core.components.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.raffa064.engine.core.api.InputAPI;
import com.raffa064.engine.core.components.Native;
import com.raffa064.engine.core.components.commons2d.Transform2D;

public abstract class RectUIBase extends Native {
	protected Transform2D transform;

	public float width;
	public float height;

	public RectUIBase(String name) {
		super(name);
		
		exportProps(
			"width", FLOAT,
			"height", FLOAT
		);
	}

	public Rectangle getRectangle() {
		Matrix3 transformed = transform.transformed();
		Vector2 pos = transformed.getTranslation(new Vector2());
		Vector2 scale = transformed.getScale(new Vector2());

		float scaledWidth = this.width * scale.x;
		float scaledHeight = this.height * scale.y;

		Rectangle rect = new Rectangle(pos.x - scaledWidth / 2, pos.y - scaledHeight / 2, scaledWidth, scaledHeight);

		return rect;
	}
	
	public Vector2 getCenter() {
		return getRectangle().getCenter(new Vector2());
	}

	@Override
	public void ready() {
		transform = (Transform2D) obj.get("Transform2D");
		Input.subscribe(this);
	}

	@Override
	public abstract boolean input(InputAPI.Event event);
	
	@Override
	public abstract void process(float delta);

	@Override
	public void exit() {
		Input.unsubscribe(this);
	}
}
