package com.raffa064.engine.modules;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;
import androidx.core.content.FileProvider;
import apk64.FileUtils;
import com.raffa064.engine.CodeActivity;
import com.raffa064.engine.EditorActivity;
import com.raffa064.engine.core.ProjectConfigs;
import com.raffa064.engine.exporter.ApkExporter;
import com.raffa064.engine.exporter.ApkExporter.ExportListener;
import com.raffa064.engine.exporter.ApkExporter.ExportProcess;
import java.io.File;

import static com.raffa064.engine.environments.editor.EditorCore.*;

public class EditorModule implements Module, ExportListener {
	private EditorActivity activity;
	
	public File engineDir;
	public File projectDir;
	private boolean isExporting;
	private boolean isInCodeEditor;

	public EditorModule(EditorActivity activity) {
		this.activity = activity;
	}
	
    @Override
	public Object onGet(int action, Object[] params) {
		switch (action) {
			case GET_PROJECT_DIR: return projectDir;
			case GET_IS_EXPORTING_PROJECT: return isExporting;
			case GET_IS_IN_CODE_EDITOR: return isExporting;
		}

		return null;
	}

	@Override
	public void onEvent(int event, Object[] params) {
		switch (event) {
			case EVENT_ERROR:
				String message = (String) params[0];
				Exception error = null;
				
				if (params[1] != null) {
					error = (Exception) params[1];
				}
				
				error(message, error);
				break;
			case EVENT_CHANGE_ENGINE_DIR:
				changeEngineDir((File) params[0]);
				break;
			case EVENT_OPEN_PROJECT:
				openProject((File) params[0]);
				break;
			case EVENT_EXPORT_PROJECT:
				exportProject();
				break;
			case EVENT_INSTALL_PROJECT:
				installProject();
				break;
			case EVENT_OPEN_CODE_EDITOR:
				openCodeActivity();
				break;
			case EVENT_CODE_EDITOR_CLOSED:
				isInCodeEditor = false;
				break;
		}
	}
	
	@Override
	public void onSucess() {
		// Log sucess
	}
	
	@Override
	public void onError(Throwable error) {
		// Log error
	}

	@Override
	public void onFinished() {
		isExporting = false;
	}
	
	public void error(String message, Exception error) {
		if (error != null) {
			message = String.format(message, error.toString());
		}

		final String finalMessage = message;
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(activity, finalMessage, Toast.LENGTH_LONG).show();
			}
		});
	}
	
	private void changeEngineDir(File engineDir) {
		if (!engineDir.exists()) {
			engineDir.mkdir();
		}

		this.engineDir = engineDir;
	}
	
	private void openProject(File projectDir) {
		if (!projectDir.exists()) {
			core.event(EVENT_ERROR, "Project don't exists");
			return;
		}
		
		this.projectDir = projectDir;
	}

	public void exportProject() {
		if (isExporting) return;

		try {
			File buildDir = new File(engineDir, ".build");
			File outputFile = new File(projectDir, "game.apk");

			buildDir.mkdir(); // create build directory

			FileUtils.deleteFiles(outputFile); // delete old apk

			ApkExporter exporter = new ApkExporter(activity, buildDir);
			ProjectConfigs projectConfigs = (ProjectConfigs) core.get(GET_PROJECT_CONFIGS);
			ExportProcess process = exporter.exportProject(projectConfigs, outputFile);
			process.setListener(this);

			isExporting = true;
		} catch (final Exception e) {
			error("Export error: %s", e);
		}
	}

	public void installProject() {
		// Code from (sajad abbasi): https://stackoverflow.com/questions/47964308/intent-to-install-apk-on-android-n

		File apkFile = new File(projectDir, "game.apk");
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			Uri apkUri = FileProvider.getUriForFile(activity, "com.raffa064.engine.provider", apkFile);
			Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
			intent.setData(apkUri);
			intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			activity.startActivity(intent);
		} else {
			Uri apkUri = Uri.fromFile(apkFile);
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			activity.startActivity(intent);
		}
	}
	
	public void openCodeActivity() {
		if (isInCodeEditor) return;
		
		Intent intent = new Intent(activity, CodeActivity.class);
		intent.putExtra("project", projectDir.toString());		
		activity.startActivityForResult(intent, EditorActivity.OPEN_CODE_EDITOR);
	}
}
