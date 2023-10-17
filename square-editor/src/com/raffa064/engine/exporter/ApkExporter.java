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

	public ExportProcess exportProject(File projectDir, File outputFile) throws Exception {
		ProjectInfo projectInfo = new ProjectInfo(projectDir);

		ExportProcess process = exportProject(projectInfo, outputFile);
		return process;
	}

	public ExportProcess exportProject(ProjectInfo projectInfo, File outputFile) throws Exception {
		extractAsset("template.apk", templateFile);
		extractAsset("default.keystore", keyStoreFile);
		
		String keyAlias = "alias";
		String keyPassword = "android";

		if (projectInfo.customKeytore != null) {
			try {
				File customKeystoreFile = new File(projectInfo.customKeytore);
				JSONObject keystoreJson = new JSONObject(FileUtils.readFileString(customKeystoreFile));

				File keystore = new File(keystoreJson.getString("path"));

				if (keystore.exists()) {
					keyStoreFile = keystore;
					keyAlias = keystoreJson.getString("alias");
					keyPassword = keystoreJson.getString("password");
				}
				
				keystoreJson.get("path");
			} catch (Exception e) {
				throw new Exception("Invalid custom keystore");
			}
		}

		Apk64Configs configs = new Apk64Configs();
		configs.setTemplateFile(templateFile);
		configs.setOutputDir(outputDir);
		configs.setKeyStoreFile(keyStoreFile, keyAlias, keyPassword);
		configs.setOutputFile(outputFile);

		ExportProcess process = createExportProcess(configs, projectInfo);
		process.start();

		return process;
	}

	public ExportProcess createExportProcess(Apk64Configs configs, ProjectInfo projectInfo) {
		Apk64 apk64 = new Apk64();

		ExportProcess process = new ExportProcess(activity, apk64, configs, projectInfo, buildDir);

		return process;
	}

	public static class ProjectInfo {
		public String name;
		public String packageName;
		public int versionCode;
		public String versionName;
		public String customKeytore;
		public File icon;
		public List<String> permissions = new ArrayList<>();
		public File projectDir;

		public ProjectInfo(File projectDir) throws Exception {
			this.projectDir = projectDir;
			
			reloadData();
		}

		public void reloadData() throws Exception {
			File cfgFile = new File(projectDir, "config.cfg");
			JSONObject configs = new JSONObject(FileUtils.readFileString(cfgFile));

			name = configs.getString("name");
			packageName = configs.getString("package");
			versionCode = configs.getInt("versionCode");
			versionName = configs.getString("versionName");
			icon = new File(projectDir, configs.getString("icon"));

			if (configs.has("customKeystore")) {
				customKeytore = configs.getString("customKeystore");
			}

//			TODO: permissions;
		}
	}

	public static class ExportProcess extends Thread {
		private Activity activity;
		private Apk64 apk64;
		private Apk64Configs configs;
		private ProjectInfo projectInfo;
		private File buildDir;
		private ExportListener listener;

		public ExportProcess(Activity activity, Apk64 apk64, Apk64Configs configs, ProjectInfo projectInfo, File buildDir) {
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

				apk64.replaceDrawable("ic_launcher.png", projectInfo.icon);
				apk64.replaceDrawable("ic_launcher.jpg", projectInfo.icon);

				for (String permission : projectInfo.permissions) {
					apk64.addPermission(permission);
				}

				apk64.addToAssets(projectInfo.projectDir);
				
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
