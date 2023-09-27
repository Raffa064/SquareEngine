package com.raffa064.engine.core;

import com.raffa064.engine.core.api.ComponentList;
import com.raffa064.engine.core.components.DynamicBody;
import com.raffa064.engine.core.components.Image;
import com.raffa064.engine.core.components.KinematicBody;
import com.raffa064.engine.core.components.StaticBody;
import com.raffa064.engine.core.components.Transform2D;

/* All fields that extends Native will be injected in ComponentAPI */

public class StandardComponents implements ComponentList {
	public Transform2D transform2D;
	public Image image;
	public StaticBody staticBody;
	public DynamicBody dynamicBody;
	public KinematicBody kinematicBody;
	public String exemplo;
}
