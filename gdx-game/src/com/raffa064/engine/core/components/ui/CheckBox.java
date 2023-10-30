package com.raffa064.engine.core.components.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.raffa064.engine.core.api.InputAPI;
import com.raffa064.engine.core.components.Native;
import com.raffa064.engine.core.components.commons2d.Transform2D;

public class CheckBox extends SingleTouchRectUiBase {
	public boolean checked;
	public Texture off;
	public Texture on;

	public CheckBox() {
		super("CheckBox");

		exportProps(
			"checked", BOOLEAN,
			"off", TEXTURE,
			"on", TEXTURE
		);
	}

	@Override
	protected boolean onTouchDown() {
		checked = !checked;
		return true;
	}

	@Override
	protected boolean onTouchDrag() {
		return true;
	}

	@Override
	protected boolean onTouchUp() {
		return true;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public boolean checked() {
		return checked;
	}

	@Override
	public void process(float delta) {
		Texture texture = checked() ? on : off;

		Rectangle rect = getRectangle();
		batch.draw(texture, rect.x, rect.y, rect.width, rect.height);

		super.process(delta);
	}
}
