package com.raffa064.engine.core;

import com.raffa064.engine.core.components.Native;
import com.raffa064.engine.core.components.Script;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
	Engine Objects class.
	An object is a set of components and other objects, 
	that can be a behavior, sprite, character, etc.
*/

public class GameObject {
	protected App app;
	protected boolean isReady;
	
	private String name = "Unknown";
	private List<GameObject> children = new ArrayList<>();
	private List<GameObject> childrenToAdd = new ArrayList<>();
	private List<GameObject> childrenToSort = new ArrayList<>();
	private List<Component> componentList = new ArrayList<>();
	private boolean queuedFree;
	private int zIndex = 0;
	private String tag;
	private List<String> groups = new ArrayList<>();

	public GameObject parent;
	
	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getTag() {
		return tag;
	}

	public void setZIndex(int zIndex) {
		this.zIndex = zIndex;
		
		if (parent != null) {
			parent.requestSortIndexes(this);
		}
	}

	public int getZIndex() {
		return zIndex;
	}
	
	public void requestSortIndexes(GameObject child) {
		childrenToSort.add(child);
	}
	
	private void addChildByIndex(GameObject child) {
		for (int i = 0; i < children.size(); i++) {
			if (child.zIndex >= children.get(i).zIndex) {
				children.add(i, child);
				return;
			}
		}
		
		children.add(child);
	}

	public List<Component> getComponents() {
		return componentList;
	}

	public List<GameObject> getChildren() {
		return children;
	}

	public void setApp(App app) {
		this.app = app;
	}
	
	private String increseNumber(String name) {
		Matcher matcher = Pattern.compile("\\([0-9]+\\)").matcher(name);

		if (matcher.find()) {
			int start = matcher.start();
			int end = matcher.end();

			int counter = Integer.parseInt(name.substring(start+1, end-1)) + 1;
			name = name.substring(0, start) + "(" + counter + ")" + name.substring(end, name.length());
		} else {
			name = name + " (1)";
		}
		
		return name;
	} 
	
	private void checkName(GameObject child) {
		for (GameObject c : children) {
			if (c == child) continue;
			
			if (c.name.equals(child.name)) {
				child.name = increseNumber(child.name);
				checkName(child);
			}
		}
	}

	public void setName(String name) {
		this.name = name;
		
		if (parent != null) {
			parent.checkName(this);
		}
	}

	public String getName() {
		return name;
	}

	public GameObject child(String name) {
		for (int i = children.size()-1; i >= 0; i--) {
			GameObject child = children.get(i);
			
			if (child.name.equals(name)) {
				return child;
			}
		}

		return null;
	}

	public void addChild(GameObject child) {
		childrenToAdd.add(child);
	}
	
	private void forceAddChild(GameObject child) {
		checkName(child);
		addChildByIndex(child);
		child.setApp(app);
		child.parent = this;

		if (isReady) child.ready();
	}

	public <T extends Component> void add(T component) {
		componentList.add(component);
	}

	public Object get(String name) {
		for (Component component : componentList) {
			if (component.name.equals(name)) {
				if (component instanceof Script) {
					return ((Script) component).script.objectScope;
				}
				return component;
			}
		}

		return null;
	}

	public boolean has(String name) {
		for (Component component : componentList) {
			if (component.name.equals(name)) {
				return true;
			}
		}
		
		return false;
	}

	public void queueFree() {
		queuedFree = true;
	}

	public boolean isQueuedFree() {
		return queuedFree;
	}

	public void ready() {
		isReady = true;
		
		for (int i = 0; i < componentList.size(); i++) {
			Component component = componentList.get(i);
			component.set("obj", this);
			
			if (component instanceof Native) {
				app.injectDependencies((Native) component);
			}
			
			component.ready();
		}
		
		for (int i = children.size()-1; i >= 0; i--) {
			GameObject child = children.get(i);
			child.ready();
		}
	}

	public void process(float delta) {
		for (int i = 0; i < childrenToAdd.size(); i++) {
			GameObject child = childrenToAdd.get(i);
			forceAddChild(child);
		}
		
		childrenToAdd.clear();
		
		for (GameObject child : childrenToSort) {
			children.remove(child);
			addChildByIndex(child);
		}
		
		childrenToSort.clear();
		
		for (int i = 0; i < componentList.size(); i++) {
			Component component = componentList.get(i);
			component.process(delta);
		}

		for (int i = children.size() - 1; i >= 0; i--) {
			GameObject child = children.get(i);
			child.process(delta);

			if (child.isQueuedFree()) {
				children.remove(child);
				child.exit();
				i = Math.min(i + 1, children.size() - 1);
				
				if (children.isEmpty()) {
					break;
				}
			}
		}
	}

	public void startCollision(GameObject other) {}
	
	public void endCollision(GameObject other) {}

	public void exit() {
		for (int i = 0; i < componentList.size(); i++) {
			Component component = componentList.get(i);
			component.exit();
		}
		
		for (int i = children.size()-1; i >= 0; i--) {
			GameObject child = children.get(i);
			child.exit();
		}
	}
}
