package com.raffa064.engine.modules;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;
import androidx.core.content.FileProvider;
import com.raffa064.engine.CodeActivity;
import com.raffa064.engine.EditorActivity;
import com.raffa064.engine.R;
import com.raffa064.engine.core.ProjectConfigs;
import com.raffa064.engine.exporter.ApkExporter;
import com.raffa064.engine.exporter.ExportListener;
import com.raffa064.engine.exporter.ExportProcess;
import java.io.File;

import static com.raffa064.engine.environments.editor.EditorCore.*;
import java.io.FileNotFoundException;

public class EditorModule implements Module, ExportListener {
	private final String BUILD_DIRECTORY_NAME = ".build";
	private final String APK_MIMETYPE = "application/vnd.android.package-archive";
	private final String FILE_PROVIDER_AUTHORITY = "com.raffa064.engine.provider";
	private final String EXPORTED_APK_NAME = "game.apk";

	private EditorActivity activity;
	
	private File engineDir;
	private File projectDir;
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
			case EVENT_CHANGE_ENGINE_DIR:
				changeEngineDir((File) params[0]);
				break;
			case EVENT_RELOAD_PROJECT:
				activity.clearNotifications();
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
		activity.createNotification(R.drawable.gmd_check_circle, "Export Sucessfull", "The generated apk file is in your project folder.");
	}
	
	@Override
	public void onError(Throwable error) {
		activity.createNotification(R.drawable.gmd_error, "Export Fail", "An unexpected error uccurred when exporting apk file:\n" + getLogs(error));
	}

	@Override
	public void onFinished() {
		isExporting = false;
	}
	
	
	private void changeEngineDir(File engineDir) {
		if (!engineDir.exists()) {
			engineDir.mkdir();
		}

		this.engineDir = engineDir;
	}
	
	private void openProject(File projectDir) {
		if (!projectDir.exists()) {
			activity.createNotification(R.drawable.gmd_error, "Unknown Project", "Project folder don't exists:\n" + projectDir.getAbsolutePath());
			return;
		}
		
		this.projectDir = projectDir;
	}

	private void exportProject() {
		if (isExporting) return;

		try {
			File buildDir = new File(engineDir, BUILD_DIRECTORY_NAME);
			File outputFile = new File(projectDir, EXPORTED_APK_NAME);

			ApkExporter exporter = new ApkExporter(activity, buildDir);
			ProjectConfigs projectConfigs = (ProjectConfigs) core.get(GET_PROJECT_CONFIGS);
			ExportProcess process = exporter.exportProject(projectConfigs, outputFile);
			process.setListener(this);

			isExporting = true;
		} catch (Exception e) {
			activity.createNotification(R.drawable.gmd_error, "Export error", "Error while creating export process:\n" + getLogs(e));
		}
	}

	private void installProject() {
		// Code from (sajad abbasi): https://stackoverflow.com/questions/47964308/intent-to-install-apk-on-android-n

		File apkFile = new File(projectDir, EXPORTED_APK_NAME);
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			installByFileProvider(apkFile);
		} else {
			installByActionView(apkFile);
		}
	}

	private void installByFileProvider(File apkFile) {
		Uri apkUri = FileProvider.getUriForFile(activity, FILE_PROVIDER_AUTHORITY, apkFile);
		Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
		intent.setData(apkUri);
		intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		activity.startActivity(intent);
	}

	private void installByActionView(File apkFile) {
		Uri apkUri = Uri.fromFile(apkFile);
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(apkUri, APK_MIMETYPE);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		activity.startActivity(intent);
	}

	private void openCodeActivity() {
		if (isInCodeEditor) return;
		
		isInCodeEditor = true;
		Intent intent = new Intent(activity, CodeActivity.class);
		intent.putExtra(CodeActivity.EXTRA_PROJECT_PATH, projectDir.toString());		
		activity.startActivityForResult(intent, EditorActivity.OPEN_CODE_EDITOR);
	}
	
	private String getLogs(Throwable error) {
		String msg = "Error Message: " + error;

		for (StackTraceElement  ste : error.getStackTrace()) {
			msg += "\n" + ste;
		}
		
		return msg;
	}
}
