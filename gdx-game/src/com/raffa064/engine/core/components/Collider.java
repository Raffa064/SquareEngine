package com.raffa064.engine.core.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.raffa064.engine.core.api.CollisionAPI.Areas;
import java.util.List;

public class Collider extends Native {
	public Transform2D transform;
	public Areas areas;
	
    public Collider() {
		super("Collider");
	}   
	
	@Override
	public void ready() {
		areas = Collision.createAreas(obj);
	}

	@Override
	public void process(float delta) {
		if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.C)) {
			batch.flush();
			
			Rectangle transformed = new Rectangle();
			for (Rectangle rect : areas.rects) {
				areas.transformRect(rect, transformed);
				
				shape.setColor(Color.RED);
				shape.rect(transformed.x, transformed.y, transformed.width, transformed.height);
			}
		}
	}
	
	public void moveAndCollide(Vector2 velocity) {
		transform.pos.add(velocity);
		
		List<Areas> collided = Collision.getCollided(areas);
		Rectangle rect = new Rectangle();
		for (Areas other : collided) {
			for (Rectangle r : other.rects) {
				other.transformRect(r, rect);
				
				//calculta colisao
			}
		}
	}

	@Override
	public void exit() {
		Collision.removeArea(areas);
	}
}
