package com.raffa064.engine.core.api;

import com.raffa064.engine.core.App;
import com.raffa064.engine.core.components.commons2d.Trigger;
import com.raffa064.engine.core.components.commons2d.Trigger.ListenerFunction;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import org.mozilla.javascript.InterpretedFunction;
import com.raffa064.engine.core.components.commons2d.Trigger.ListenerFunction;
import org.mozilla.javascript.Scriptable;

public class TriggerAPI extends API {
	public HashMap<String, List<Trigger>> layers;
	
	public TriggerAPI(App app) {
		super(app);
	}

	@Override
	public API.APIState createState() {
		return buildState(
			layers = new HashMap<>()
		);
	}

	@Override
	public void useState(API.APIState state) {
		layers = state.next();
	}
	
	public List<Trigger> layer(String name) {
		List<Trigger> layer = layers.getOrDefault(name, null);
		
		if (layer == null) {
			layer = new ArrayList<Trigger>();
			layers.put(name, layer);
		}
		
		return layer;
	}
	
	public void subscribe(Trigger trigger) {
		for (String layerName : trigger.layers) {
			List<Trigger> layer = layer(layerName);
			
			layer.add(trigger);
		}
	}
	
	public void update() {
		for (Map.Entry<String, List<Trigger>> entry : layers.entrySet()) {
			List<Trigger> _layer = entry.getValue();
			
			for (Trigger a : _layer) {
				List<Trigger> lCollided = a.collided;
				a.collided = new ArrayList<>();
				
				for (String layerName : a.masks) {
					List<Trigger> layer = layer(layerName);
		
					for (Trigger b : layer) {
						if (a == b) continue;
						
						if (a.overlap(b)) {
							a.collided.add(b);
							if (!lCollided.contains(b)) {
								callListeners(a.enterListeners, b);
							}
						} else {
							if (lCollided.contains(b)) {
								callListeners(a.exitListeners, b);
							}
						}
					}
				}
			}
		}
	}	
	
	public ListenerFunction createListener(Scriptable obj, String functionName) {
		ListenerFunction listener = new ListenerFunction(
			app.scriptEngine.ctx,
			obj, 
			functionName
		);
		
		return listener;
	}
	
	private void callListeners(List<ListenerFunction> listeners, Trigger trigger) {
		for (ListenerFunction listener : listeners) {
			listener.on(trigger);
		}
	}

	public void unsubscribe(Trigger trigger) {
		for (String layerName : trigger.layers) {
			List<Trigger> layer = layer(layerName);

			layer.remove(trigger);
		}
	}
}
