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
import com.raffa064.engine.modules.EditorModule;

public class EditorActivity extends AndroidApplication {
	public static final int OPEN_CODE_EDITOR = 1;
	
	public File SQUARE_ENGINE_DIR;

	// Editor Runtime
	private EditorCore editor;
	private EditorGame editorGame;
	
	private EditorModule editorModule;
	public File projectDir;
	
	// Views
	private RelativeLayout rootLayout;
	private LinearLayout gameParent;

	public EditorActivity() {
		editor = EditorCore.instance();
		editorModule = new EditorModule(this);
		editor.add(editorModule);
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

		createFloatBubble();
		//createFloatWindows();
	}

	@Override
	protected void onResume() {
		super.onResume();
		editor.event(EVENT_RELOAD_PROJECT); // Resquest reload
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
			case OPEN_CODE_EDITOR:
				editor.event(EVENT_CODE_EDITOR_CLOSED);
				break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		editor.remove(editorModule);
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
			editor.event(EVENT_ERROR, "Project don't exists");
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
		} catch (Exception e) {
			editor.event(EVENT_ERROR, "Error on initialize game: %s", e);
		}
	}

	private void createFloatBubble() {
		FloatBubble bubble = new FloatBubble(this);
		bubble.addIntoView(rootLayout);
		
		bubble.setOnBubbleActionListener(new FloatBubble.OnBubbleActionListener() {
			@Override
			public void onAction(int action) {
				editor.event(action);
			}
		});

		bubble.addAction(EVENT_EXPORT_PROJECT, R.drawable.gmd_unarchive, "Export APK");
		bubble.addAction(EVENT_INSTALL_PROJECT, R.drawable.gmd_adb, "Install APK");
		bubble.addAction(EVENT_OPEN_CODE_EDITOR, R.drawable.gmd_code, "Code editor");
		bubble.addAction(EVENT_OPEN_SCENE_TREE, R.drawable.gmd_photo_size_select_actual, "Scene Tree");
		bubble.addAction(EVENT_OPEN_INSPECTOR, R.drawable.gmd_visibility, "Inspector");
	}
}
