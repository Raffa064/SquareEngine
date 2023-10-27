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
import com.raffa064.engine.ui.FloatWindow;

public class EditorActivity extends AndroidApplication {
	public static final int OPEN_CODE_EDITOR = 1;
	
	// Editor Runtime
	private EditorCore core;
	private EditorGame editorGame;
	
	// Modules
	private EditorModule editorModule;
	
	// Views
	private RelativeLayout rootLayout;
	private LinearLayout gameParent;

	public EditorActivity() {
		core = EditorCore.instance();
		editorModule = new EditorModule(this);
		core.add(editorModule);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getActionBar().hide();
		setContentView(R.layout.activity_main);

		setupDirs();
		initializeViews();
		initializeGame();

		createFloatBubble();
		createFloatWindows();
	}

	@Override
	protected void onResume() {
		super.onResume();
		core.event(EVENT_RELOAD_PROJECT); // Resquest reload
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
			case OPEN_CODE_EDITOR:
				core.event(EVENT_CODE_EDITOR_CLOSED);
				break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		core.remove(editorModule);
	}

 	private void setupDirs() {
		File engineDir = new File(Environment.getExternalStorageDirectory(), "SquareEngine");
		File projectDir = new File(engineDir, "project"); // TODO: change to target project name
		
		core.event(EVENT_CHANGE_ENGINE_DIR, engineDir);
		core.event(EVENT_OPEN_PROJECT, projectDir); 
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
			core.event(EVENT_ERROR, "Error on initialize game: %s", e);
		}
	}

	private void createFloatBubble() {
		FloatBubble bubble = new FloatBubble(this);
		
		bubble.addIntoView(rootLayout);
		bubble.setOnBubbleActionListener(new FloatBubble.OnBubbleActionListener() {
			@Override
			public void onAction(int action) {
				core.event(action);
			}
		});

		bubble.addAction(EVENT_EXPORT_PROJECT, R.drawable.gmd_unarchive, "Export APK");
		bubble.addAction(EVENT_INSTALL_PROJECT, R.drawable.gmd_adb, "Install APK");
		bubble.addAction(EVENT_OPEN_CODE_EDITOR, R.drawable.gmd_code, "Code editor");
		bubble.addAction(EVENT_OPEN_SCENE_TREE, R.drawable.gmd_photo_size_select_actual, "Scene Tree");
		bubble.addAction(EVENT_OPEN_INSPECTOR, R.drawable.gmd_visibility, "Inspector");
	}
	
	private void createFloatWindows() {
		FloatWindow window = new FloatWindow(this);
		window.load("file:///android_asset/scene-tree/scene-tree.html");
		window.title("Title");
		window.position(0, 0);
		window.width(500);
		window.height(500);
		window.addIntoView(rootLayout);
		window.toggleWindow();
	}
}
