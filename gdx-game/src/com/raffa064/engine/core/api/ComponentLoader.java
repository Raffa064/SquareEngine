package com.raffa064.engine.core.api;

import com.raffa064.engine.core.App;
import com.raffa064.engine.core.Component;
import com.raffa064.engine.core.ScriptEngine;
import com.raffa064.engine.core.components.Image;
import com.raffa064.engine.core.components.Script;
import com.raffa064.engine.core.components.Transform2D;
import java.util.HashMap;

public class ComponentLoader {
	private App app;
	private HashMap<String, Class> nativeComponents = new HashMap<>();

	public ComponentLoader(App app) {
		this.app = app;
		
		loadNative(
			Transform2D.class,
			Image.class
		);
	}
	
	public void loadNative(Class... classes) {
		for (Class componentClass : classes) {
			nativeComponents.put(componentClass.getSimpleName(), componentClass);
		}
	}
	
	public void loadScript(String fileName, String script) {
		app.scriptEngine.compile(script, fileName);
	}
	
	public void loadScript(String script) {
		app.scriptEngine.compile(script);
	}
	
	public Object create(String name) {
		if (nativeComponents.containsKey(name)) {
			try {
				return (Component) nativeComponents.get(name).newInstance();
			} catch (Exception e) {}
		}
		
		ScriptEngine.CompiledScript script = app.scriptEngine.newObject(name);
		Script scriptCom = new Script(script);
		return scriptCom;
	}
	
	public ComponentLoader js() {
		ComponentLoader componentLoaderJS = new ComponentLoader(app) {
			@Override
			public Object create(String name) {
				Object component = super.create(name);

				if (component instanceof Script) {
					return ((Script) component).js();
				}	

				return component;
			}
		};
		
		componentLoaderJS.nativeComponents = nativeComponents;
		
		return componentLoaderJS;
	}
}
