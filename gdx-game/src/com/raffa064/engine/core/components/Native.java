package com.raffa064.engine.core.components;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.raffa064.engine.core.App;
import com.raffa064.engine.core.Component;
import com.raffa064.engine.core.api.AssetsAPI;
import com.raffa064.engine.core.api.ComponentAPI;
import com.raffa064.engine.core.api.LoggerAPI;
import com.raffa064.engine.core.api.SceneAPI;
import com.raffa064.engine.core.api.TagAPI;
import com.raffa064.engine.core.api.GroupAPI;

public abstract class Native extends Component {
	public GroupAPI Group;
	public TagAPI Tag;
	public SceneAPI Scene;
	public ComponentAPI Component;
	public AssetsAPI Assets;
	public LoggerAPI Logger;
	
	public SpriteBatch batch;
	public ShapeRenderer shape;
	
	public Native(String name) {
		super(name);
	}
	
	public void setApp(App app) {
		if (app == null) return;
		
		
		app.injectAPIs(this);
	}
	
	public void exportProp(String name, String type) {
		Component.ExportedProp exportedProp = new ExportedProp(name, type);
		exportedProps.add(exportedProp);
	}
	
	public void exportProps(String... nameAndTypeList) {
		for (int i = 0; i < nameAndTypeList.length; i += 2) {
			exportProp(nameAndTypeList[i], nameAndTypeList[i+1]);
		}
	}
}
