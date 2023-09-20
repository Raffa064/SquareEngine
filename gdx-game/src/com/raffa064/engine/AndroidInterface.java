package com.raffa064.engine;

public interface AndroidInterface {

	public boolean isEditorMode();
	
	public void openInspector();
	
	public void openSceneTree();
	
	public void setDebugText(String text);
	
	public String getProjectPath();

	public void openEditor();
	
	public boolean isOpennedEditor();
}
