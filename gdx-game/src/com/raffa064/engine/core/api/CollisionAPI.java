package com.raffa064.engine.core.api;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.raffa064.engine.core.App;
import com.raffa064.engine.core.GameObject;

/*
	API to provide features related to physics.
	
	NOTE: Underdevelopmenst, it aren't completed, and it can be broken
*/

public class CollisionAPI extends API {
	public World world;
	public Box2DDebugRenderer b2ddr = new Box2DDebugRenderer();

	public CollisionAPI(App app) {
		super(app);
	}

	@Override
	public API.APIState createState() {
		world = new World(new Vector2(0, -.98f), false);
		world.setContactListener(new ContactListener() {
				@Override
				public void beginContact(Contact contact) {
					Body bodyA = contact.getFixtureA().getBody();
					Body bodyB = contact.getFixtureB().getBody();

					GameObject objA = (GameObject) bodyA.getUserData();
					GameObject objB = (GameObject) bodyB.getUserData();

					objA.startCollision(objB);
					objA.endCollision(objA);
				}

				@Override
				public void endContact(Contact p1) {
				}

				@Override
				public void preSolve(Contact p1, Manifold p2) {
				}

				@Override
				public void postSolve(Contact p1, ContactImpulse p2) {
				}
			});

		return buildState(
			world
		);
	}

	@Override
	public void useState(API.APIState state) {
		world = state.next();
	}

	public void stepPhysics(float delta) {
		world.step(delta, 6, 2);
	}

	public void renderDebug() {
		b2ddr.render(world, app.Scene.getCamera().combined);
	}
}
