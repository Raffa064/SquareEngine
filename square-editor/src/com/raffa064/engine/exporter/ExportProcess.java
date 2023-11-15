package com.raffa064.engine.exporter;

import android.app.Activity;
import apk64.Apk64;
import apk64.Apk64Configs;
import apk64.FileUtils;
import com.raffa064.engine.core.ProjectConfigs;
import java.io.File;

public class ExportProcess extends Thread {
	private Activity activity;
	private Apk64 apk64;
	private Apk64Configs configs;
	private ProjectConfigs projectInfo;
	private File buildDir;
	private ExportListener listener;

	public ExportProcess(Activity activity, Apk64 apk64, Apk64Configs configs, ProjectConfigs projectInfo, File buildDir) {
		this.activity = activity;
		this.apk64 = apk64;
		this.configs = configs;
		this.projectInfo = projectInfo;
		this.buildDir = buildDir;
	}

	public void setListener(ExportListener listener) {
		this.listener = listener;
	}

	@Override
	public void run() {
		try {
			// Start export process
			apk64.setConfigs(configs);
			apk64.loadTemplate();

			applyMetadata(); // Change template icon, name, version, etc...
			injectSources(); // Add game code and assets into apk
			
			// Finish apk (compress and sign)
			apk64.finish();
			
			// Clear trash
			FileUtils.deleteFiles(buildDir);

			if (listener != null) {
				listener.onSucess();
			}
		} catch (Error | Exception e) {
			if (listener != null) {
				listener.onError(e);
			}
		}

		if (listener != null) {
			listener.onFinished();
		}
	}

	private void applyMetadata() {
		//Apply changes
		apk64.changeAppName(projectInfo.name);
		apk64.changePackage(projectInfo.packageName);
		apk64.changeVersion(projectInfo.versionCode, projectInfo.versionName);

		if (projectInfo.icon.exists()) {
			apk64.replaceAppIcon(projectInfo.icon);
		}

		for (String permission : projectInfo.permissions) {
			apk64.addPermission(permission);
		}
	}
	
	private void injectSources() throws Exception {
		File assets = apk64.getAssets();
		File projectDir = projectInfo.getProjectDirAsFile();
		
		apk64.addToAssets(projectDir); // Move project dir to assets folder
		
		// Rename project dir to "assets/project"
		File assets_projectDir = new File(assets, projectDir.getName());
		File assets_renamedProjectDir = new File(assets, "project");
		assets_projectDir.renameTo(assets_renamedProjectDir);
		assets_projectDir = assets_renamedProjectDir;

		ProjectOptimizer.optimizeScripts(assets_projectDir);

		int key = projectInfo.packageName.hashCode();
		ProjectSafer.safe(assets_projectDir, key);
		
		File editorFile = new File(assets_projectDir, ".editor");
		FileUtils.deleteFiles(editorFile); // remove .editor file from exported apk
	}
}
