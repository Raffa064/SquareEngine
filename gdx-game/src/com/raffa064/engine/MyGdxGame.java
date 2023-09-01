package com.raffa064.engine;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.raffa064.engine.core.App;
import com.raffa064.engine.core.GameObject;
import com.raffa064.engine.core.components.Image;
import com.raffa064.engine.core.components.Scene;
import com.raffa064.engine.core.components.Script;
import com.raffa064.engine.core.components.Transform2D;

public class MyGdxGame extends Game {
	private App app;
	
	public MyGdxGame() {
	}
	
	@Override
	public void create() {
		app = new App();
		app.init();
		app.loadProject(Gdx.files.internal("project"));

		Scene scene = new Scene();
		app.setScene(scene);
		
		GameObject bg = new GameObject();
		
		Transform2D bgTransform = (Transform2D) app.Component.create("Transform2D");
		bgTransform.get("pos", Vector2.class).set(512, 300);
		bg.add(bgTransform);
		
		Image bgImage = (Image) app.Component.create("Image");
		bgImage.texturePath = "project/bg.jpg";
		bg.add(bgImage);
		
		scene.addChild(bg);
		
		GameObject obj = new GameObject();
		obj.setZIndex(1);
		
		Transform2D transform = (Transform2D) app.Component.create("Transform2D");
		transform.get("pos", Vector2.class).set(100, 100);
		obj.add(transform);
		
		Image img = (Image) app.Component.create("Image");
		img.texturePath = "project/square.png";
		obj.add(img);
		
		Script script = (Script) app.Component.create("MoveComponent");
		obj.add(script);
		
		Script script2 = (Script) app.Component.create("ParticleEmitter");
		script2.set("spawnTime", 0.01);
		script2.set("duration", 2);
		obj.add(script2);
		
		scene.addChild(obj);
	}

	@Override
	public void render() {
		app.render(Gdx.graphics.getDeltaTime());
	}

	@Override
	public void resize(int width, int height) {
		app.resize(width, height);
	}
}
