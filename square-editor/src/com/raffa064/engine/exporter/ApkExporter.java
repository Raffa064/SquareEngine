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
	public final String TEMPLATE_FILE_NAME = "template.apk";
	public final String OUTPUT_DIR_NAME = "output";
	public final String KEYSTORE_FILE_NAME = "keystore";
	public final int KEY_ALIAS = 0;
	public final int KEY_PASSWORD = 1;
	
	private int bufferSize = 4096;
	
	private Activity activity;
	private File buildDir;
	private File templateFile;
	private File outputDir;
	private File keyStoreFile;

	public ApkExporter(Activity activity, File buildDir) {
		this.activity = activity;
		this.buildDir = buildDir;

		templateFile = new File(buildDir, TEMPLATE_FILE_NAME);
		outputDir = new File(buildDir, OUTPUT_DIR_NAME);
		keyStoreFile = new File(buildDir, KEYSTORE_FILE_NAME);
	}

	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	public int getBufferSize() {
		return bufferSize;
	}
	
	public ExportProcess exportProject(ProjectConfigs projectConfigs, File outputFile) throws Exception {
		// Extract template and defult keystore from assets
		extractAsset("template.apk", templateFile);
		extractAsset("default.keystore", keyStoreFile);

		String keyAlias = "alias";
		String keyPassword = "android";

		if (projectConfigs.customKeytore != null) {
			String[] data = loadCustomKeystoreData(projectConfigs, keyAlias, keyPassword);
			keyAlias = data[KEY_ALIAS];
			keyPassword = data[KEY_PASSWORD];
		}

		// Setup Apk64 configurations
		Apk64Configs configs = new Apk64Configs();
		configs.setTemplateFile(templateFile);
		configs.setOutputDir(outputDir);
		configs.setKeyStoreFile(keyStoreFile, keyAlias, keyPassword);
		configs.setOutputFile(outputFile);

		// Create and start export process (in parallel)
		ExportProcess process = createExportProcess(configs, projectConfigs);
		process.start();

		return process;
	}

	private void extractAsset(String assetPath, File targetFile) throws Exception {
		InputStream is = activity.getAssets().open(assetPath);

		FileOutputStream fos = new FileOutputStream(targetFile);
		BufferedOutputStream bos = new BufferedOutputStream(fos);

		byte[] buffer = new byte[bufferSize];
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

	private String[] loadCustomKeystoreData(ProjectConfigs projectConfigs, String keyAlias, String keyPassword) throws Exception {
		try {
			File customKeystoreFile = new File(projectConfigs.customKeytore);
			JSONObject keystoreJson = new JSONObject(FileUtils.readFileString(customKeystoreFile));

			File keystore = new File(JSONUtils.getString(keystoreJson, "path", ""));

			if (keystore.exists()) {
				keyStoreFile = keystore; // Change from default to custom keystore
				
				keyAlias = keystoreJson.getString("alias");
				keyPassword = keystoreJson.getString("password");
			}
		} catch (Exception e) {
			throw new Exception("Invalid custom keystore");
		}
		
		String[] data = new String[2];
		data[KEY_ALIAS] = keyAlias;
		data[KEY_PASSWORD] = keyPassword;
		
		return data;
	}

	private ExportProcess createExportProcess(Apk64Configs configs, ProjectConfigs projectConfigs) {
		Apk64 apk64 = new Apk64(bufferSize);
		ExportProcess process = new ExportProcess(activity, apk64, configs, projectConfigs, buildDir);
		return process;
	}
	
}
