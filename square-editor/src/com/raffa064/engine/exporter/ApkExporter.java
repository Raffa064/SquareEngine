package com.raffa064.engine.exporter;

import android.app.Activity;
import android.widget.Toast;
import apk64.Apk64;
import apk64.Apk64Configs;
import apk64.FileUtils;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;
import com.raffa064.engine.core.json.JSONUtils;
import com.raffa064.engine.core.ProjectConfigs;

public class ApkExporter {
	public Activity activity;
	public File buildDir;
	public File templateFile;
	public File outputDir;
	public File keyStoreFile;

	public ApkExporter(Activity activity, File buildDir) {
		this.activity = activity;
		this.buildDir = buildDir;

		templateFile = new File(buildDir, "template.apk");
		outputDir = new File(buildDir, "output");
		keyStoreFile = new File(buildDir, "keystore");
	}

	public void extractAsset(String assetPath, File targetFile) throws Exception {
		InputStream is = activity.getAssets().open(assetPath);

		FileOutputStream fos = new FileOutputStream(targetFile);
		BufferedOutputStream bos = new BufferedOutputStream(fos);

		byte[] buffer = new byte[1024];
		int length;
		while ((length = is.read(buffer)) > 0) {
			bos.write(buffer, 0, length);
		}

		bos.flush();
		bos.close();

		fos.flush();
		fos.close();

		is.close();
	}

	public ExportProcess exportProject(ProjectConfigs projectConfigs, File outputFile) throws Exception {
		extractAsset("template.apk", templateFile);
		extractAsset("default.keystore", keyStoreFile);
		
		String keyAlias = "alias";
		String keyPassword = "android";

		if (projectConfigs.customKeytore != null) {
			try {
				File customKeystoreFile = new File(projectConfigs.customKeytore);
				JSONObject keystoreJson = new JSONObject(FileUtils.readFileString(customKeystoreFile));

				File keystore = new File(JSONUtils.getString(keystoreJson, "path", ""));

				if (keystore.exists()) {
					keyStoreFile = keystore;
					keyAlias = keystoreJson.getString("alias");
					keyPassword = keystoreJson.getString("password");
				}
			} catch (Exception e) {
				throw new Exception("Invalid custom keystore");
			}
		}

		Apk64Configs configs = new Apk64Configs();
		configs.setTemplateFile(templateFile);
		configs.setOutputDir(outputDir);
		configs.setKeyStoreFile(keyStoreFile, keyAlias, keyPassword);
		configs.setOutputFile(outputFile);

		ExportProcess process = createExportProcess(configs, projectConfigs);
		process.start();

		return process;
	}

	public ExportProcess createExportProcess(Apk64Configs configs, ProjectConfigs projectConfigs) {
		Apk64 apk64 = new Apk64();

		ExportProcess process = new ExportProcess(activity, apk64, configs, projectConfigs, buildDir);

		return process;
	}

	public static class ExportProcess extends Thread {
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
				apk64.setConfigs(configs);
				apk64.loadTemplate();

				//Apply changes
				apk64.changeAppName(projectInfo.name);
				apk64.changePackage(projectInfo.packageName);
				apk64.changeVersion(projectInfo.versionCode, projectInfo.versionName);

				if (projectInfo.icon.exists()) {
					apk64.replaceDrawable("ic_launcher.png", projectInfo.icon);
					apk64.replaceDrawable("ic_launcher.jpg", projectInfo.icon);
				}

				for (String permission : projectInfo.permissions) {
					apk64.addPermission(permission);
				}

				apk64.addToAssets(projectInfo.getProjectDirAsFile()); // Move project dir to assets folder
				
				File assets_projectDir = new File(apk64.getAssets(), "project");
				ProjectOptimizer.optimizeScripts(assets_projectDir);
				
				int key = projectInfo.packageName.hashCode();
				ProjectSafer.safe(assets_projectDir, key);
				
				apk64.finish();

				FileUtils.deleteFiles(buildDir);

				if (listener != null) {
					listener.sucess();
				}
			} catch (final Exception e) {
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(activity, "Export error: " + e, Toast.LENGTH_LONG).show();
					}
				});
			}
		}
	}

	public static interface ExportListener {
		public void sucess();
	}
}
