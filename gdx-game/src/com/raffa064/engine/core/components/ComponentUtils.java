package com.raffa064.engine.core.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector2;
import com.raffa064.engine.core.api.AssetsAPI;

public class ComponentUtils {
    public static void boundingBox(AssetsAPI Assets, SpriteBatch batch, float x, float y, float width, float height, float scaleX, float scaleY, float rotation) {
		Color batchColor = batch.getColor();
		batch.setColor(Color.WHITE);

		NinePatch ninePatch = Assets.ninePatch(Gdx.files.internal("gizmo/bounding-box.png"), 3, 3, 3, 3);
		ninePatch.draw(
			batch,
			x - width / 2 * scaleX,
			y - height / 2 * scaleY,
			width / 2 * scaleX,
			height / 2 * scaleY,
			width * scaleX,
			height * scaleY,
			1,
			1,
			rotation
		);
		
		batch.setColor(batchColor);
	}
	
	public static void boundingBox(AssetsAPI Assets, SpriteBatch batch, float width, float height, Matrix3 matrix, boolean allowRotation) {
		Vector2 tmp = new Vector2();
		
		matrix.getTranslation(tmp);
		float x = tmp.x;
		float y = tmp.y;
		
		matrix.getScale(tmp);
		float scaleX = tmp.x;
		float scaleY = tmp.y;
		
		float rotation = allowRotation? matrix.getRotation() : 0;
		
		boundingBox(Assets, batch, x, y, width, height, scaleX, scaleY, rotation);
	}
	
	public static void boundingBox(AssetsAPI Assets, SpriteBatch batch, float width, float height, Matrix3 matrix) {
		boundingBox(Assets, batch, width, height, matrix, true);		
	}
}
