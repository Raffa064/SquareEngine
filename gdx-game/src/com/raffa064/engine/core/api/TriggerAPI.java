package com.raffa064.engine.core.api;

import com.raffa064.engine.core.App;
import com.raffa064.engine.core.components.commons2d.Trigger;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

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
		return layers.getOrDefault(name, new ArrayList<Trigger>());
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
					List<Trigger> layer = entry.getValue();
		
					for (Trigger b : layer) {
						if (a == b) continue;
						
						/* 
							if collided
								if lastCollided
									colliding
								else
									entered
							else
								if lastCollided
									exited
						*/
					}
				}
			}
		}
	}	

	public void unsubscribe(Trigger trigger) {
		for (String layerName : trigger.layers) {
			List<Trigger> layer = layer(layerName);

			layer.remove(trigger);
		}
	}
}
