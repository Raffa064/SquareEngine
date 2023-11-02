package com.raffa064.engine.core.components;

import com.raffa064.engine.core.App;
import com.raffa064.engine.core.Component;
import com.raffa064.engine.core.ScriptEngine.CompiledScript;
import com.raffa064.engine.core.api.InputAPI;
import java.util.Map;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public class Script extends Component {
	private App app;
	public CompiledScript script;

	public Script(App app, CompiledScript script) {
		super(script.name);
		
		this.app = app;
		this.script = script;

		set("THIS", this);
		
		for (Map.Entry<String, String> entry : script.exportedProps.entrySet()) {
			ExportedProp prop = new ExportedProp(entry.getKey(), entry.getValue());
			exportedProps.add(prop);
		}
	}

	@Override
	public void setInputPriority(int inputPriority) {
		super.setInputPriority(inputPriority);
		
		app.Input.sortInputComponents();
	}
	
	public Scriptable js() {
		return script.objectScope;
	}

	@Override
	public <T> T get(String key, Class<T> type) {
		return (T) ScriptableObject.getProperty(script.objectScope, key);
	}

	@Override
	public Object get(String key) {
		return ScriptableObject.getProperty(script.objectScope, key);
	}

	@Override
	public void set(String key, Object value) {
		ScriptableObject.putProperty(script.objectScope, key, value);
	}

	public Object call(String methodName, Object... params) {
		if (ScriptableObject.hasProperty(script.objectScope, methodName)) {
			return ScriptableObject.callMethod(script.objectScope, methodName, params);	
		} 
		
		return null;
	}
	
	public Object eval(String code) {
		return app.scriptEngine.eval(script.objectScope, code);
	}
	
	@Override
	public void ready() {
		call("ready");
	}
	
	@Override
	public boolean input(InputAPI.Event event) {
		Object result = call("input", event);
		
		if (result instanceof Boolean) {
			return (boolean) result;
		}
		
		return false;
	}
	
	@Override
	public void process(float delta) {
		call("process", delta);
	}

	@Override
	public void exit() {
		call("exit");
	}
}
