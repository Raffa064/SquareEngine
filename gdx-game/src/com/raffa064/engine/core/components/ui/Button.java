package com.raffa064.engine.core.components.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.raffa064.engine.core.api.InputAPI;
import com.raffa064.engine.core.components.Native;
import com.raffa064.engine.core.components.commons2d.Transform2D;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

public class Button extends SingleTouchRectUiBase {
	public Texture normal;
	public Texture pressed;
	public String fontPath;
	public String text;
	private BitmapFont font;
	private GlyphLayout layout = new GlyphLayout();
	
	public Button() {
		super("Button");
		
		exportProps(
			"normal", TEXTURE,
			"pressed", TEXTURE,
			"fontPath", STRING,
			"text", STRING
		);
	}

	@Override
	protected boolean onTouchDown() {
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

	@Override
	public void process(float delta) {
		Texture texture = pressed() ? pressed : normal;
		
		Rectangle rect = getRectangle();
		batch.draw(texture, rect.x, rect.y, rect.width, rect.height);
		
		if (font == null) {
			font = Assets.font(fontPath);
		} 
		
		if (font != null) {
			float scaleX = font.getScaleX();
			float scaleY = font.getScaleY();
			
			font.getData().setScale(rect.width / width, rect.height / height);
			layout.setText(font, text, font.getColor(), rect.width, Align.center, true);
			font.draw(batch, text, rect.x, rect.y + rect.height / 2 + layout.height / 2, rect.width, Align.center, true);
			
			font.getData().setScale(scaleX, scaleY);
		}

		super.process(delta);
	}
}
