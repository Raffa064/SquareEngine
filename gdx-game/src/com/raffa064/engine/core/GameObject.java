package com.raffa064.engine.core;

import com.raffa064.engine.core.components.Native;
import com.raffa064.engine.core.components.Script;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

/*
 Engine Objects class.
 An object is a set of components and other objects, 
 that can be a behavior, sprite, character, etc.
 */

public class GameObject {
//	private static final int ACTION_ADD_COMPONENT = 0;
//	private static final int ACTION_ADD_CHILD = 1;
//	private static final int ACTION_CHANGE_CHILD_INDEX = 2;
//	private static final int ACTION_REMOVE_CHILD = 3;
//	
	protected App app;
	protected boolean isReady;

	private String name = "Unknown";
	private List<GameObject> children = new ArrayList<>();
	private List<GameObject> nextFrameChildren = new ArrayList<>();
	private List<Component> components = new ArrayList<>();
	private boolean queuedFree;
	private int zIndex = 0;
//	private String tag;
//	private List<String> groups = new ArrayList<>();
//	
	public GameObject parent;

	public GameObject() {}
	
	public void setApp(App app) {
		this.app = app;
		
		for (GameObject child : children) {
			child.setApp(app);
		}
	}

	public void add(Component component) {
		components.add(component);
		
		component.obj = this;
		component.set("obj", this);
		
		if (isReady) {
			// TODO: need to call component.ready (in next frame?)
		}
	}
	
	public void add(Scriptable scriptComponent) {
		Script component = (Script) ScriptableObject.getProperty(scriptComponent, "THIS"); // Get Java instance from JS object
		add(component);
	}
	
	public Object get(String componentName) {
		for (Component component : components) {
			if (componentName.equals(component.name)) {
				if (component instanceof Script) {
					// In theory, only scripts will acess script components directily, 
					// so it convert Script component into your JS instance
					return ((Script)component).script.objectScope; 
				}
				
				return component;
			}
		}
		
		return null;
	}
	
	public List<Component> getComponents() {
		return components;
	}
	
	public void setZIndex(int zIndex) {
		this.zIndex = zIndex;
		
		if (isReady) {
			parent.children.remove(this);
			parent.addChildByIndex(this);
		}
	}
	
	public int getZIndex() {
		return zIndex;
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
	
	public void addChild(GameObject child) {
		child.setApp(app);
		child.parent = this;

		if (isReady) {
			nextFrameChildren.add(child); // When obj is ready, it will add the child at the beging of next frame (in process method)
		} else {
			addChildByIndex(child);
		}
	}	
	
	public Object getChild(String childName) {
		for (GameObject child: children) {
			if (childName.equals(child.name)) {
				return child;
			}
		}
		
		return null;
	}
	
	public List<GameObject> getChildren() {
		return children;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;

		if (isReady && parent != null) {
			parent.checkChildName(this);
		}
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

	private void checkChildName(GameObject child) {
		for (GameObject c : children) {
			if (c == child) continue;

			if (c.name.equals(child.name)) {
				child.name = increseNumber(child.name);
				checkChildName(child);
			}
		}
	}
	
	public void ready() {
		isReady = true;

		for (Component component : components) {
			if (component instanceof Native) {
				app.injectDependencies((Native) component);
			}
			
			component.ready();
		}		
		
		for (GameObject child : children) {
			child.ready();
		}
	}
	
	public void process(float delta, boolean editorMode) {
		for (GameObject child : nextFrameChildren) {
			checkChildName(child);
			addChildByIndex(child);
			
			child.ready();
		}
		
		nextFrameChildren.clear();
		
		for (Component component : components) {
			if (editorMode) {
				component.editor(delta);
			} else {
				component.process(delta);
			}
		}
		
		List<GameObject> trash = new ArrayList<>();
		for (GameObject child : children) {
			child.process(delta, editorMode);
			
			if (child.isQueuedFree()) {
				trash.add(child);
			}
		}
		
		for (GameObject child : trash) {
			child.exit();
			children.remove(child);
		}
	}
	
	public void queueFree() {
		queuedFree = true;
	}
	
	public boolean isQueuedFree() {
		return queuedFree;
	}
	
	public void exit() {
		for (GameObject child : children) {
			child.exit();
		}
		
		for (Component component : components) {
			component.exit();
		}
	}
}
