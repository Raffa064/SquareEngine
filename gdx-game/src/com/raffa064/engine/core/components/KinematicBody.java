package com.raffa064.engine.core.components;

import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class KinematicBody extends Collider {
	public KinematicBody() {
		super("KinematicBody", BodyType.KinematicBody);
	}
}
