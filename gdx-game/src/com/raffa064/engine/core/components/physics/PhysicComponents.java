package com.raffa064.engine.core.components.physics;

import com.raffa064.engine.core.api.ComponentList;

public interface PhysicComponents extends ComponentList {
	public StaticBody staticBody;
	public DynamicBody dynamicBody;
	public KinematicBody kinematicBody;
}
