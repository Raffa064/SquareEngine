package com.raffa064.engine.core;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import java.util.ArrayList;
import java.util.List;
import com.raffa064.engine.core.api.InputAPI.Event;

/*
	Base class for components (such as Native, and ScriptComponents)
	A component is like an "behavior" that you can add into a object.
*/

public abstract class Component {
	public static final String COLOR = "COLOR";
	public static final String STRING = "STRING";
	public static final String VECTOR2 = "VECTOR2";
	public static final String FLOAT = "FLOAT";
	public static final String INTEGER = "INTEGER";
	public static final String BOOLEAN = "BOOLEAN";
	public static final String TEXTURE = "TEXTURE";
	
	public String name;
	public GameObject obj;
	public List<ExportedProp> exportedProps = new ArrayList<>();
	
	private int inputPriority;
	
	public Component(String name) {
		this.name = name;
	}

	public void setInputPriority(int inputPriority) {
		this.inputPriority = inputPriority;
	}

	public int getInputPriority() {
		return inputPriority;
	}

	public <T> T get(String key, Class<T> type) {
		try {
			return (T) this.getClass().getField(key).get(this);
		} catch (Exception e) {
			return null;
		}
	}

	public Object get(String key) {
		return get(key, Object.class);
	}

	public void set(String key, Object value) {
		try {
			this.getClass().getField(key).set(this, value);
		} catch (Exception e) {
		}
	}

	public abstract void ready();

	public abstract boolean input(Event event);
	
	public abstract void process(float delta);

	public abstract void exit();

	public static class ExportedProp {
		public String name, type;

		public ExportedProp(String name, String type) {
			this.name = name;
			this.type = type;
		}
	}
}
