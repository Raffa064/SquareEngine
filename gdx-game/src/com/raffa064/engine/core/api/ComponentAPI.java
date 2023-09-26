package com.raffa064.engine.core.api;

import com.raffa064.engine.core.App;
import com.raffa064.engine.core.ScriptEngine;
import com.raffa064.engine.core.components.DynamicBody;
import com.raffa064.engine.core.components.Image;
import com.raffa064.engine.core.components.KinematicBody;
import com.raffa064.engine.core.components.Native;
import com.raffa064.engine.core.components.Script;
import com.raffa064.engine.core.components.StaticBody;
import com.raffa064.engine.core.components.Transform2D;
import java.util.HashMap;

public class ComponentAPI extends API {
	private HashMap<String, Class> nativeComponents = new HashMap<>();

	public ComponentAPI(App app) {
		super(app);
		
		loadNative(
			Transform2D.class,
			Image.class,
			StaticBody.class,
			DynamicBody.class,
			KinematicBody.class
		);		
	}

	@Override
	public APIState createState() {
		return buildState();
	}


	@Override
	public void useState(APIState values) {
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
				Native nativeComponent = (Native) nativeComponents.get(name).newInstance();
				return nativeComponent;
			} catch (Exception e) {}
		}
		
		ScriptEngine.CompiledScript script = app.scriptEngine.newObject(name);
		Script scriptCom = new Script(script);
		return scriptCom;
	}
	
	public ComponentAPI js() {
		ComponentAPI componentLoaderJS = new ComponentAPI(app) {
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
