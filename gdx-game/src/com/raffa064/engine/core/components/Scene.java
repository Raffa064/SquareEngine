package com.raffa064.engine.core.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.raffa064.engine.core.GameObject;
import java.util.HashMap;

public class Scene extends GameObject {
	public Color backgroundColor = Color.BLACK;
	public OrthographicCamera camera;
	public SpriteBatch batch;
	public ShapeRenderer shape;
	
	public Scene() {}

	@Override
	public int getZIndex() {
		return 0;
	}

	@Override
	public void setZIndex(int zIndex) {}
	
	public void init() {
		setupCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		batch = new SpriteBatch();
		shape = new ShapeRenderer();
	}

	public void setupCamera(float screenWidth, float screenHeight) {
		float width = app.viewportWidth;
		float height = app.viewportHeight;

		if (app.keepWidth) {
			float ratio = screenHeight / screenWidth;
			height = width * ratio;
		} else {
			float ratio = screenWidth / screenHeight;
			width = height * ratio;
		}

		if (camera == null) {
			camera = new OrthographicCamera(width, height);	
		}

		camera.viewportWidth = width;
		camera.viewportHeight = height;
		camera.setToOrtho(false, width, height);
	}

	@Override
	public void process(float delta) {
		camera.update();

		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.setColor(Color.WHITE);

		shape.begin(ShapeRenderer.ShapeType.Filled);
		shape.setProjectionMatrix(camera.combined);
		shape.setColor(Color.WHITE);

		super.process(delta);

		shape.end();

		batch.end();
	}
}
