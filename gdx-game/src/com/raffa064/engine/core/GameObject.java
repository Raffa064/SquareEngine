package com.raffa064.engine.core;

import com.raffa064.engine.core.components.Script;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class GameObject {
	protected App app;
	private String name;
	private List<GameObject> children = new ArrayList<>();
	private List<Component> componentList = new ArrayList<>();
	public GameObject parent;
	private boolean queuedFree;
	private int zIndex = 0;

	public void setZIndex(int zIndex) {
		this.zIndex = zIndex;
		
		if (parent != null) {
			parent.changeIndex(this);
		}
	}

	public int getZIndex() {
		return zIndex;
	}
	
	public void changeIndex(GameObject child) {
		children.remove(child);
		addChildByIndex(child);
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
		checkName(child);
		addChildByIndex(child);
		child.setApp(app);
		child.ready();
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


	public void queueFree() {
		queuedFree = true;
	}

	public boolean isQueuedFree() {
		return queuedFree;
	}

	public void ready() {
		for (int i = 0; i < componentList.size(); i++) {
			Component component = componentList.get(i);
			component.set("obj", this);
			component.set("batch", app.getSceneBatch());
			component.set("shape", app.getSceneShapeRender());
			
			component.ready();
		}
	}

	public void process(float delta) {
		for (int i = 0; i < componentList.size(); i++) {
			Component component = componentList.get(i);
			component.process(delta);
		}

		for (int i = children.size()-1; i >= 0; i--) {
			GameObject child = children.get(i);
			child.process(delta);

			if (child.isQueuedFree()) {
				children.remove(child);
				child.exit();
				i++;
			}
		}
	}

	public void exit() {
		for (int i = 0; i < componentList.size(); i++) {
			Component component = componentList.get(i);
			component.exit();
		}
	}
}
