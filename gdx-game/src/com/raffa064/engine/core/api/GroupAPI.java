package com.raffa064.engine.core.api;

import com.raffa064.engine.core.App;
import com.raffa064.engine.core.GameObject;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class GroupAPI extends API{
	private HashMap<String, List<GameObject>> groupMap = new HashMap<>();

	public GroupAPI(App app) {
		super(app);
	}

	public void add(GameObject obj, String group) {
		List<GameObject> list = groupMap.getOrDefault(group, new ArrayList<GameObject>());

		list.add(obj);
		
		groupMap.put(group, list);
	}
	
	public void add(GameObject obj, String... groups) {
		for (String group : groups) {
			add(obj, group);
		}
	}

	public List<GameObject> get(String group) {
		return groupMap.getOrDefault(group, new ArrayList<GameObject>());
	}

	public void remove(GameObject obj, String group) {
		get(group).remove(obj);
	}
	
	public void remove(GameObject obj, String... groups) {
		for (String group : groups) {
			remove(obj, group);
		}
	}
}
