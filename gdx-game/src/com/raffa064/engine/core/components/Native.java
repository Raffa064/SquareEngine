package com.raffa064.engine.core.components;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.raffa064.engine.core.App;
import com.raffa064.engine.core.Component;

public abstract class Native extends Component {
	protected SpriteBatch batch;
	protected ShapeRenderer shape;
	
	public Native(String name) {
		super(name);
	}
	
	public void setApp(App app) {
		if (app == null) return;
		
		batch = app.scene.batch;
		shape = app.scene.shape;
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
