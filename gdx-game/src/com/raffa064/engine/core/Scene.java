package com.raffa064.engine.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.raffa064.engine.core.GameObject;
import com.raffa064.engine.core.api.API;
import com.raffa064.engine.core.api.API.APIState;
import java.util.HashMap;

/*
	Runtime scene, it stores all scene objects and properties, like batch, shape, background and camera
*/

public class Scene extends GameObject {
	public Color backgroundColor = Color.BLACK;
	public OrthographicCamera camera;
	public SpriteBatch batch;
	public ShapeRenderer shape;
	public HashMap<API, APIState> states;
	public String orientation;

	public void loadSceneState() {
		if (states == null) {
			states = new HashMap<>();
			
			for (API api : app.apiList) {
				states.put(api, api.createState().first());
			}
			
			return;
		}
		
		for (API api : app.apiList) {
			api.useState(states.get(api).first());
		}
	}

	@Override
	public int getZIndex() {
		return 0;
	}

	@Override
	public void setZIndex(int zIndex) {}
	
	public void init() {
		loadSceneState();
		
		if (!isReady) {
			setupCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			batch = new SpriteBatch();
			shape = new ShapeRenderer();
			ready();
		}
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
	public void process(float delta, boolean editorMode) {
		camera.update();

		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.setColor(Color.WHITE);

		shape.begin(ShapeRenderer.ShapeType.Filled);
		shape.setProjectionMatrix(camera.combined);
		shape.setColor(Color.WHITE);

		super.process(delta, editorMode);

		shape.end();

		batch.end();
	}
}
