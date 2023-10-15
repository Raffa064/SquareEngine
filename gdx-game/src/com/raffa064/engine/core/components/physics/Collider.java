package com.raffa064.engine.core.components.physics;

import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.raffa064.engine.core.collision.Shape;
import com.raffa064.engine.core.components.Native;
import com.raffa064.engine.core.components.commons2d.Transform2D;
import com.badlogic.gdx.math.MathUtils;

public class Collider extends Native {
	public Transform2D transform;
	
	public World world;
	public Body body;
	public Shape shape = new Shape();
	public PolygonShape pShape;
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
		
		Matrix3 transformed = transform.transformed();
		bodyDef.position.set(transformed.getTranslation(new Vector2()));
		bodyDef.angle = transformed.getRotation();
		
		body = world.createBody(bodyDef);
		body.setUserData(obj);
		
		MassData massData = new MassData();
		massData.mass = mass;
		body.setMassData(massData);
		
		pShape = new PolygonShape();
		updateCollisionShape();
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = pShape;
		fixtureDef.density = density;
		fixtureDef.filter.categoryBits = categoryBits;
		fixtureDef.filter.maskBits = maskBits;
		fixtureDef.friction = friction;
		fixtureDef.isSensor = isSensor;
		fixtureDef.restitution = restitution;
	
		fixture = body.createFixture(fixtureDef);
	}

	private void updateCollisionShape() {
		Vector2 scale = new Vector2();
		transform.transformed().getScale(scale);
		pShape.set(shape.scaled(scale));
	}
	
	public void setShape(Shape shape) {
		this.shape = shape;
		updateCollisionShape();
	}
	
	public void setShape(float[] shape) {
		this.shape.shape = shape;
		updateCollisionShape();
	}

	@Override
	public void process(float delta) {
		transform.pos.set(body.getPosition());
		transform.rotation = body.getAngle() / (MathUtils.PI/180);
	}

	@Override
	public void exit() {
		world.destroyBody(body);
		pShape.dispose();
	}
}
