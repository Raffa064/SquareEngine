package com.raffa064.engine.core.api;

import com.raffa064.engine.core.App;
import com.raffa064.engine.core.GameObject;
import java.util.HashMap;

/*
	Provide tag feature for objects
*/

public class TagAPI extends API {
	private HashMap<String, GameObject> tagMap;
	
	public TagAPI(App app) {
		super(app);
	}
	
	@Override
	public APIState createState() {
		return buildState(
			tagMap = new HashMap<>()
		);
	}


	@Override
	public void useState(APIState values) {
		tagMap = values.next();
	}
	
	public boolean use(GameObject obj, String tag) {
		if (tagMap.containsKey(tag)) {
			return false;
		}
		
		tagMap.put(tag, obj);
		return true;
	}
	
	public GameObject find(String tag) {
		return tagMap.getOrDefault(tag, null);
	}
	
	public GameObject free(String tag) {
		GameObject obj = find(tag);
		
		tagMap.remove(tag);
		
		return obj;
	}
}
