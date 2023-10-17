package com.raffa064.engine.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.raffa064.engine.core.json.JSONUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

public class ProjectConfigs {
	// Load configs
	public String projectPath;
	public boolean isAbsolutePath;
	public boolean autoTranspile;
	public boolean isEncrypted;
	public int decodeKey;
	
	// Export configs
	public String name;
	public String packageName;
	public int versionCode;
	public String versionName;
	public String customKeytore;
	public File icon;
	public List<String> permissions = new ArrayList<>();

	// Runtime configs
	public String mainScene;
	public float viewportWidth;
	public float viewportHeight;
	public boolean keepWidth;

	// Runtime mode
	public ProjectConfigs(String projectPath, boolean isAbsolutePath, boolean autoTranspile, boolean isEncrypted, int decodeKey) throws Exception {
		this.projectPath = projectPath;
		this.isAbsolutePath = isAbsolutePath;
		this.autoTranspile = autoTranspile;
		this.isEncrypted = isEncrypted;
		this.decodeKey = decodeKey;
		
		reloadData();
	}
	
	// Dev mode
	public ProjectConfigs(String projectPath) throws Exception {
		this(projectPath, true, true, false, 0);
	}
	
	public void reloadData() throws Exception {
		String json = null;
		
		FileHandle cfgFile = findConfigFile();
		json = cfgFile.readString();
		
		JSONObject configs = new JSONObject(json);
		
		if (isAbsolutePath) {
			name = JSONUtils.getString(configs, "name", "Unknown");
			packageName = JSONUtils.getString(configs, "package", "com.example.package");
			versionCode = JSONUtils.getInt(configs, "versionCode", 1);
			versionName = JSONUtils.getString(configs, "versionName", "1.0");
			icon = new File(projectPath, JSONUtils.getString(configs, "icon", ""));

			if (configs.has("customKeystore")) {
				customKeytore = configs.getString("customKeystore");
			}

			// TODO: permissions;
		}
		
		mainScene = JSONUtils.getString(configs, "mainScene", "main");

		JSONObject viewport = JSONUtils.getJSONObject(configs, "viewport", new JSONObject());
		viewportWidth = (float) JSONUtils.getDouble(viewport, "width", 1024);
		viewportHeight = (float) JSONUtils.getDouble(viewport, "height", 720);
		keepWidth = JSONUtils.getBoolean(viewport, "keepWidth", true);
	}
	
	public FileHandle findConfigFile() throws Exception {
		FileHandle projectDir = getProjectDir();
		
		for (FileHandle file : projectDir.list()) {
			if (file.name().endsWith(".cfg")) {
				return file;
			}
		}
		
		throw new Exception("No config file!");
	}

	public FileHandle getProjectDir() {
		if (isAbsolutePath) {
			return Gdx.files.absolute(projectPath);
		}
		
		return Gdx.files.internal(projectPath);
	}
	
	public File getProjectDirAsFile() {
		return new File(projectPath);
	}
}
