package com.raffa064.engine;

import java.io.File;

public interface EditorInterface {
	public File projectDir;
	public boolean stable;
	
	public void onError(String message, Exception error);
	
	/*public boolean isEditorMode();
	
	public void openInspector();
	
	public void openSceneTree();
	
	public void setDebugText(String text);
	
	public String getProjectPath();

	public void openEditor();
	
	public boolean isOpennedEditor();
	
	public void exportProject();
	
	public boolean isExporting();
	*/
}
