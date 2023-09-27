package com.raffa064.engine.core.api;

import com.raffa064.engine.core.App;
import com.raffa064.engine.core.ScriptEngine;
import com.raffa064.engine.core.components.Native;
import com.raffa064.engine.core.components.Script;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ComponentAPI extends API {
	private HashMap<String, Class> nativeComponents = new HashMap<>();

	public ComponentAPI(App app) {
		super(app);		
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
			app.scriptEngine.inject("_"+componentClass.getSimpleName(), componentClass.getSimpleName()); //Inject class name into script scope
			nativeComponents.put(componentClass.getSimpleName(), componentClass);
		}
	}

	public void loadComponentList(Class<? extends ComponentList> from) {
		Field[] declaredFields = from.getFields();

		for (Field f : declaredFields) {
			if (Native.class.isAssignableFrom(f.getType())) {
				System.out.println("Injecting "+f.getType().getSimpleName()+" as native component");
//				loadNative(f.getType());
			}
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
