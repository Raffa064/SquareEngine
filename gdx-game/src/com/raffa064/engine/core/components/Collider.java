package com.raffa064.engine.core.components;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.raffa064.engine.core.GameObject;

public class Collider extends Native {
	public Transform2D transform;
	
	public World world;
	public Body body;
	public PolygonShape shape;
	public Fixture fixture;
	public BodyType type;
	
	public float mass;
	public boolean active;
	public boolean allowSleep;
	public float density;
    public short categoryBits;
    public short maskBits;
    public float friction;
    public boolean isSensor;
    public float restitution;

	public Collider(String name, BodyType type) {
		super(name);
		this.type = type;
	}   
	
	@Override
	public void ready() {
		transform = (Transform2D) obj.get("Transform2D");
		world = Collision.world;
		
		BodyDef bodyDef = new BodyDef();
		bodyDef.active = active;
		bodyDef.allowSleep = allowSleep;
		bodyDef.type = type;
		bodyDef.position.set(transform.pos);
		
		body = world.createBody(bodyDef);
		body.setUserData(obj);
		
		MassData massData = new MassData();
		massData.mass = mass;
		body.setMassData(massData);
		
		shape = new PolygonShape();
		// TODO: shape editor...
		shape.setAsBox(100, 100);
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = density;
		fixtureDef.filter.categoryBits = categoryBits;
		fixtureDef.filter.maskBits = maskBits;
		fixtureDef.friction = friction;
		fixtureDef.isSensor = isSensor;
		fixtureDef.restitution = restitution;
	
		fixture = body.createFixture(fixtureDef);
	}

	@Override
	public void process(float delta) {
		fixture.getShape().setRadius(2);
		transform.pos.set(body.getPosition());
	}

	@Override
	public void exit() {
		world.destroyBody(body);
		shape.dispose();
	}
}
