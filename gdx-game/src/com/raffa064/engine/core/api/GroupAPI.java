package com.raffa064.engine.core.api;

import com.raffa064.engine.core.App;
import com.raffa064.engine.core.GameObject;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

/*
	Object group API. Used to provide groups feature.
*/

public class GroupAPI extends API {
	private HashMap<String, List<GameObject>> groupMap;

	public GroupAPI(App app) {
		super(app);
	}

	@Override
	public APIState createState() {
		return buildState(
			groupMap = new HashMap<>()
		);
	}

	@Override
	public void useState(APIState values) {
		groupMap = values.next();
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
