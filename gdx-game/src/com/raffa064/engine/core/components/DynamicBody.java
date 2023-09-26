package com.raffa064.engine.core.components;

import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class DynamicBody extends Collider {
	public DynamicBody() {
		super("DynamicBody", BodyType.DynamicBody);
	}
}
