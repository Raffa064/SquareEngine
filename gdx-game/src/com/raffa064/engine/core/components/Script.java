package com.raffa064.engine.core.components;

import com.raffa064.engine.core.Component;
import com.raffa064.engine.core.ScriptEngine.CompiledScript;
import java.util.Map;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Scriptable;

public class Script extends Component {
	public CompiledScript script;

	public Script(CompiledScript script) {
		super(script.name);
		
		this.script = script;
		set("_javaInstance", this);
		
		for (Map.Entry<String, String> entry : script.exportedProps.entrySet()) {
			ExportedProp prop = new ExportedProp(entry.getKey(), entry.getValue());
			exportedProps.add(prop);
		}
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
		return ScriptableObject.callMethod(script.objectScope, methodName, params);
	}
	
	@Override
	public void ready() {
		call("ready");
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
