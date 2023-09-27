package com.raffa064.engine.core.components;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.raffa064.engine.core.Component;
import com.raffa064.engine.core.api.AssetsAPI;
import com.raffa064.engine.core.api.CollisionAPI;
import com.raffa064.engine.core.api.ComponentAPI;
import com.raffa064.engine.core.api.DebugAPI;
import com.raffa064.engine.core.api.GroupAPI;
import com.raffa064.engine.core.api.InputAPI;
import com.raffa064.engine.core.api.LoggerAPI;
import com.raffa064.engine.core.api.SceneAPI;
import com.raffa064.engine.core.api.TagAPI;
import java.lang.reflect.Field;
import java.util.ArrayList;

public abstract class Native extends Component  {
	public DebugAPI Debug;
	public InputAPI Input;
	public CollisionAPI Collision;
	public GroupAPI Group;
	public TagAPI Tag;
	public SceneAPI Scene;
	public ComponentAPI Component;
	public AssetsAPI Assets;
	public LoggerAPI Logger;
	public SpriteBatch batch;
	public ShapeRenderer shape;
	
	public Native() {
		super("Unknown");
	}
	
	public Native(String name) {
		super(name);
	}
	
	public void exportProps(String... nameAndTypeList) {
		for (int i = 0; i < nameAndTypeList.length; i += 2) {
			String name = nameAndTypeList[i];
			String type = nameAndTypeList[i+1];
			
			ExportedProp exportedProp = new ExportedProp(name, type);
			exportedProps.add(exportedProp);
		}
	}
}
