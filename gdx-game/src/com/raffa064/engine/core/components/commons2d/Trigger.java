package com.raffa064.engine.core.components.commons2d;

/*
	Add a detection shape to the object
*/

import com.raffa064.engine.core.components.Native;
import java.util.List;
import org.mozilla.javascript.Function;
import java.util.HashMap;
import java.util.ArrayList;
import org.mozilla.javascript.InterpretedFunction;

public abstract class Trigger extends Native {
	public List<String> layers = new ArrayList<>();
	public List<String> masks = new ArrayList<>();
	public List<Trigger> collided = new ArrayList<>();

	public List<InterpretedFunction> enterListeners = new ArrayList<>();
	public List<InterpretedFunction> exitListeners = new ArrayList<>();
	
	public Trigger(String name) {
		super(name);
	}
	
	public void subscribe(String event, InterpretedFunction function) {
		switch (event) {
			case "enter": enterListeners.add(function); return;
			case "exit": exitListeners.add(function); return;
		}
		
		throw new Error("Unknown trigger event: '" + event + "'");
	}
	
	public void unsubscribe(String event, InterpretedFunction function) {
		switch (event) {
			case "enter": enterListeners.remove(function); return;
			case "exit": exitListeners.remove(function); return;
		}

		throw new Error("Unknown trigger event: '" + event + "'");
	}

	@Override
	public void ready() {
		Trigger.subscribe(this);
	}
	
	public abstract boolean overlap(Trigger trigger);
	
	@Override
	public abstract void process(float delta);

	@Override
	public void exit() {
		Trigger.unsubscribe(this);
	}
}
