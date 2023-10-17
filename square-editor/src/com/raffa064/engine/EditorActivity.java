package com.raffa064.engine;

import android.content.Intent;
import android.content.pm.PackageInstaller.Session;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import apk64.FileUtils;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.raffa064.engine.exporter.ApkExporter;
import com.raffa064.engine.exporter.ApkExporter.ExportListener;
import com.raffa064.engine.exporter.ApkExporter.ExportProcess;
import com.raffa064.engine.ui.FloatBubble;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import static com.raffa064.engine.EditorCore.*;
import androidx.core.content.FileProvider;
import com.raffa064.engine.core.ProjectConfigs;

public class EditorActivity extends AndroidApplication implements Module, ExportListener {
	public File SQUARE_ENGINE_DIR;

	public EditorCore editor;

	private File projectDir;
	private EditorGame editorGame;
	private boolean isExporting;

	public RelativeLayout rootLayout;
	public LinearLayout gameParent;

	public EditorActivity() {
		editor = EditorCore.instance();
		editor.add(this);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getActionBar().hide();
		setContentView(R.layout.activity_main);

		createEngineDir();
		openProject("project");
		initializeViews();
		initializeGame();
	}

	@Override
	protected void onResume() {
		super.onResume();
		editor.event(EVENT_RELOAD_PROJECT); // Resquest reload
	}

	private void error(String message, Exception error) {
		if (message.contains("%s")) {
			message = String.format(message, error.toString());
		}

		final String finalMessage = message;
		runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(EditorActivity.this, finalMessage, Toast.LENGTH_LONG).show();
				}
			});
	}

	private void error(String message) {
		error(message, new Exception());
	}

 	private void createEngineDir() {
		SQUARE_ENGINE_DIR = new File(Environment.getExternalStorageDirectory(), "SquareEngine");

		if (!SQUARE_ENGINE_DIR.exists()) {
			SQUARE_ENGINE_DIR.mkdir();
		}
	}

	private void openProject(String name) {
		projectDir = new File(SQUARE_ENGINE_DIR, name);

		if (!projectDir.exists()) {
			error("Project don't exists");
		}
	} 

	private void initializeViews() {
		rootLayout = findViewById(R.id.root_layout);
		gameParent = findViewById(R.id.game_parent);
	}

	private void initializeGame() {
		try {
			editorGame = new EditorGame();
			View gameView = initializeForView(editorGame);
			gameParent.addView(gameView);

			createFloatBubble();
			//createFloatWindows();
		} catch (Exception e) {
			error("Error on initialize game: %s", e);
		}
	}

	private void createFloatBubble() {
		FloatBubble bubble = new FloatBubble(this);
		bubble.addIntoView(rootLayout);
	}

	public void exportProject() {
		if (isExporting) return;

		try {
			File buildDir = new File(SQUARE_ENGINE_DIR, ".build");
			File outputFile = new File(projectDir, "game.apk");

			buildDir.mkdir();

			FileUtils.deleteFiles(outputFile); // delete old apk

			ApkExporter exporter = new ApkExporter(this, buildDir);
			ProjectConfigs projectConfigs = (ProjectConfigs) editor.get(GET_PROJECT_CONFIGS);
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
			Uri apkUri = FileProvider.getUriForFile(this, "com.raffa064.engine.provider", apkFile);
			Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
			intent.setData(apkUri);
			intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			startActivity(intent);
		} else {
			Uri apkUri = Uri.fromFile(apkFile);
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		}
	}
	
	public void openCodeActivity() {
		Intent intent = new Intent(this, CodeActivity.class);
		intent.putExtra("project", projectDir.toString());		
		startActivity(intent);
	}

	@Override
	public Object onGet(int action, Object[] params) {
		switch (action) {
			case GET_PROJECT_DIR: return projectDir;
			case GET_IS_EXPORTING_PROJECT: return isExporting;
		}

		return null;
	}

	@Override
	public void onEvent(int event, Object[] params) {
		switch (event) {
			case EVENT_ERROR:
				String message = (String) params[0];
				Exception error = (Exception) params[1];
				error(message, error);
				break;
			case EVENT_EXPORT_PROJECT:
				exportProject();
				break;
			case EVENT_INSTALL_PROJECT:
				installProject();
				break;
			case EVENT_OPEN_CODE:
				openCodeActivity();
				break;
		}
	}

	@Override
	public void sucess() {
		isExporting = false;
	}
}
