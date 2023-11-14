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
	private static final int ACTION_ADD_COMPONENT = 0;
	private static final int ACTION_ADD_CHILD = 1;
	private static final int ACTION_CHANGE_CHILD_INDEX = 2;
	private static final int ACTION_REMOVE_CHILD = 3;
	
	protected App app;
	protected boolean isReady;

	private String name = "Unknown";
	private List<GameObject> children = new ArrayList<>();
	private List<Component> components = new ArrayList<>();
	private List<Object[]> requestedActions = new ArrayList<>();
	private boolean queuedFree;
	private int zIndex = 0;
	private String tag;
	private List<String> groups = new ArrayList<>();
	
	public GameObject parent;
	
	private void requestAction(Object value, int action) {
		Object[] params = { value, action };
		
		requestedActions.add(params);
	}
	
	private void actionAddComponent(Component component) {
		components.add(component);
		
		if (isReady && !component.isReady()) {
			component.set("obj", this);

			if (component instanceof Native) {
				app.injectDependencies((Native) component);
			}
			
			component._ready();
		}
	}
	
	private void actionAddChild(GameObject child) {
		checkName(child);
		addChildByIndex(child);
		child.setApp(app);
		child.parent = this;

		if (isReady && !child.isReady) {
			child.ready();
		}
	}

	private void actionChangeChildIndex(GameObject child) {
		children.remove(child);
		addChildByIndex(child);
	}

	private void actionRemoveChild(GameObject child) {
		children.remove(child);
		child.exit();
	}
	
	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getTag() {
		return tag;
	}

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
		requestAction(child, ACTION_CHANGE_CHILD_INDEX);
	}

	private void addChildByIndex(GameObject child) {
		for (int i = 0; i < children.size(); i++) {
			if (child.zIndex < children.get(i).zIndex) {
				children.add(i, child);
				return;
			}
		}

		children.add(child);
	}

	public List<Component> getComponents() {
		return components;
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
		for (int i = 0; i < children.size(); i++) {
			GameObject child = children.get(i);

			if (child.name.equals(name)) {
				return child;
			}
		}

		return null;
	}

	public GameObject addChild(GameObject child) {
		requestAction(child,  ACTION_ADD_CHILD);
		return this;
	}

	public <T extends Component> GameObject add(T component) {
		requestAction(component, ACTION_ADD_COMPONENT);
		
		return this;
	}

	public Object get(String name) {
		for (Component component : components) {
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
		for (Component component : components) {
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

		for (int i = 0; i < components.size(); i++) {
			Component component = components.get(i);
			component.set("obj", this);

			if (component instanceof Native) {
				app.injectDependencies((Native) component);
			}

			component._ready();
		}

		for (int i = 0; i < children.size(); i++) {
			GameObject child = children.get(i);
			child.ready();
		}
	}

	public void process(float delta) {
		for (Object[] params : requestedActions) {
			Object value = params[0];
			int action = params[1];
			
			switch (action) {
				case ACTION_ADD_COMPONENT:
					actionAddComponent((Component) value);
					break;
				case ACTION_ADD_CHILD:
					actionAddChild((GameObject) value);
					break;
				case ACTION_CHANGE_CHILD_INDEX:
					actionChangeChildIndex((GameObject) value);
					break;
				case ACTION_REMOVE_CHILD:
					actionRemoveChild((GameObject) value);
					break;
			}
		}
		
		requestedActions.clear();
		
		for (int i = 0; i < components.size(); i++) {
			Component component = components.get(i);
			component.process(delta);
		}

		for (int i = 0; i < children.size(); i++) {
			GameObject child = children.get(i);
			child.process(delta);

			if (child.isQueuedFree()) {
				requestAction(child, ACTION_REMOVE_CHILD);
			}
		}
	}

	public void exit() {
		for (int i = 0; i < children.size(); i++) {
			GameObject child = children.get(i);
			child.exit();
		}
		
		for (int i = 0; i < components.size(); i++) {
			Component component = components.get(i);
			component.exit();
		}
	}
}
