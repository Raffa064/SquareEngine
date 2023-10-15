package com.raffa064.engine.core.components.physics;

import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class StaticBody extends Collider {
	public StaticBody() {
		super("StaticBody", BodyType.StaticBody);
	}
}
