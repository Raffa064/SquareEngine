package com.raffa064.engine.core;

import java.util.ArrayList;
import java.util.List;
import com.raffa064.engine.core.components.Script;

public class GameObject {
	protected App app;
	private String name;
	private List<GameObject> children = new ArrayList<>();
	private List<Component> componentList = new ArrayList<>();
	public GameObject parent;
	private boolean queuedFree;

	public List<Component> getComponents() {
		return componentList;
	}

	public List<GameObject> getChildren() {
		return children;
	}

	public void setApp(App app) {
		this.app = app;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public GameObject child(String name) {
		for (GameObject child : children) {
			if (child.name.equals(name)) {
				return child;
			}
		}

		return null;
	}

	public void addChild(GameObject child) {
		children.add(child);
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
		for (Component component : componentList) {
			component.set("obj", this);
			component.set("batch", app.getSceneBatch());
			component.set("shape", app.getSceneShapeRender());
			component.ready();
		}
	}

	public void process(float delta) {
		for (Component component : componentList) {
			component.process(delta);
		}

		for (int i = 0; i < children.size(); i++) {
			GameObject child = children.get(i);
			child.process(delta);

			if (child.isQueuedFree()) {
				children.remove(child);
				child.exit();
				i--;
			}
		}
	}

	public void exit() {
		for (Component component : componentList) {
			component.exit();
		}
	}
}
