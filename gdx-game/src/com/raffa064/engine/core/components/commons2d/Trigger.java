package com.raffa064.engine.core.components.commons2d;

/*
	Add a detection shape to the object
*/

import com.raffa064.engine.core.components.Native;
import java.util.ArrayList;
import java.util.List;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.InterpretedFunction;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public abstract class Trigger extends Native {
	public List<String> layers = new ArrayList<>();
	public List<String> masks = new ArrayList<>();
	public List<Trigger> collided = new ArrayList<>();

	public List<ListenerFunction> enterListeners = new ArrayList<>();
	public List<ListenerFunction> exitListeners = new ArrayList<>();
	
	public Trigger(String name) {
		super(name);
	}
	
	public void subscribe(String event, ListenerFunction function) {
		switch (event) {
			case "enter": enterListeners.add(function); return;
			case "exit": exitListeners.add(function); return;
		}
		
		throw new Error("Unknown trigger event: '" + event + "'");
	}
	
	public void subscribe(String event, Scriptable obj, String functionName) {
		ListenerFunction listener = Trigger.createListener(obj, functionName);
		
		subscribe(event, listener);
	}
	
	public void unsubscribe(String event, ListenerFunction function) {
		switch (event) {
			case "enter": enterListeners.remove(function); return;
			case "exit": exitListeners.remove(function); return;
		}

		throw new Error("Unknown trigger event: '" + event + "'");
	}
	
	public void unsubscribe(String event, Scriptable obj, String functionName) {
		ListenerFunction listener = Trigger.createListener(obj, functionName);

		unsubscribe(event, listener);
	}

	@Override
	public void ready() {
		Trigger.subscribe(this);
	}
	
	public abstract boolean overlap(Trigger trigger);
	
	@Override
	public void process(float delta) {}

	@Override
	public void exit() {
		Trigger.unsubscribe(this);
	}
	
	public static class ListenerFunction {
		protected Context ctx;
		protected Scriptable obj;
		protected String functionName;
		
		public ListenerFunction() {}

		public ListenerFunction(Context ctx, Scriptable obj, String functionName) {
			this.ctx = ctx;
			this.obj = obj;
			this.functionName = functionName;
		}
		
		public void on(Trigger trigger) {
			ScriptableObject.callMethod(ctx, obj, functionName, new Object[] { trigger });
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof ListenerFunction) {
				ListenerFunction lf = (ListenerFunction) obj;
				
				if (functionName != null) {
					if (lf.functionName == functionName) {
						return true; // Js mode
					}
				} else {
					if (this == lf) {
						return true; // Java mode
					}
				}
			}
			
			return false;
		}
	}
}
