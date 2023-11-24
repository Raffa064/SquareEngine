package com.raffa064.engine;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.raffa064.engine.environments.Android;
import com.raffa064.engine.environments.editor.EditorCore;
import com.raffa064.engine.environments.editor.EditorGame;
import com.raffa064.engine.modules.EditorModule;
import com.raffa064.engine.ui.FloatBubble;
import com.raffa064.engine.ui.FloatNotifications;
import com.raffa064.engine.ui.FloatWindow;
import java.io.File;
import java.util.Random;
import static com.raffa064.engine.environments.editor.EditorCore.*;
import com.raffa064.engine.modules.EngineDataModule;

public class EditorActivity extends AndroidApplication implements Android {
	public static final String EXTRA_PROJECT_DIR_NAME = "projectDirName";

	public static final int OPEN_CODE_EDITOR = 1;

	// Editor Runtime
	private EditorCore core;
	private EditorGame editorGame;

	// Modules
	private EditorModule editorModule;

	// Views
	private RelativeLayout rootLayout;
	private LinearLayout gameParent;
	private FloatNotifications notifications;

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

		initializeViews();
		
		setupDirs();
		initializeGame();
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

	@Override
	public void setOrientation(final String orientationName) {
		runOnUiThread(new Runnable() {
				@Override
				public void run() {
					int orientationCode = getOrientationCode(orientationName);
					setRequestedOrientation(orientationCode);
				}
			});
	}

	@Override
	public void debug(String message) {
		int lineBreak = message.indexOf("\n");
		String title = message.substring(0, lineBreak).trim();
		message = message.substring(lineBreak+1, message.length()).trim();
		
		createNotification(R.drawable.gmd_bug_report, title, message);
	}

	@Override
	public void warning(String message) {
		int lineBreak = message.indexOf("\n");
		String title = message.substring(0, lineBreak).trim();
		message = message.substring(lineBreak+1, message.length()).trim();

		// TODO: add an warning icon
		createNotification(R.drawable.gmd_notifications, title, message);
	}

	@Override
	public void error(String message, Throwable error) {
		String stack = "";
		
		int lineBreak = message.indexOf("\n");
		String title = message.substring(0, lineBreak).trim();
		message = message.substring(lineBreak+1, message.length()).trim();

		if (error != null) {
			message = String.format(message, error.toString());

			for (StackTraceElement ste : error.getStackTrace()) {
				String className = ste.getClassName();
				className = className.substring(className.lastIndexOf('.'), className.length());
				String methodName = ste.getMethodName();
				int lineNumber = ste.getLineNumber();

				stack += String.format("\nat %s.%s:%d", className, methodName, lineNumber);
			}
		}

		createNotification(R.drawable.gmd_error, title, message + stack);
		core.event(EVENT_ERROR);
	}

	private int getOrientationCode(String orientationName) {
		Object[][] map = {
			{ "behind", ActivityInfo.SCREEN_ORIENTATION_BEHIND },
			{ "fullSensor", ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR },
			{ "fullUser", ActivityInfo.SCREEN_ORIENTATION_FULL_USER },
			{ "landscape", ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE },
			{ "locked", ActivityInfo.SCREEN_ORIENTATION_LOCKED },
			{ "noSensor", ActivityInfo.SCREEN_ORIENTATION_NOSENSOR },
			{ "portrait", ActivityInfo.SCREEN_ORIENTATION_PORTRAIT },
			{ "reverseLandscape", ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE },
			{ "reversePortrait", ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT },
			{ "sensor", ActivityInfo.SCREEN_ORIENTATION_SENSOR },
			{ "sensorLandscape", ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE },
			{ "sensorPortrait", ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT },
			{ "unspecified", ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED },
			{ "user", ActivityInfo.SCREEN_ORIENTATION_USER },
			{ "userLandscape", ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE },
			{ "userPortrait", ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT },
		};

		for (Object[] entry : map) {
			String key = (String) entry[0];
			int value = (Integer) entry[1];

			if (orientationName.equals(key)) {
				return value;
			}
		}

		return ActivityInfo.SCREEN_ORIENTATION_SENSOR;
	}

 	private void setupDirs() {
		File engineDir = new File(Environment.getExternalStorageDirectory(), "SquareEngine");
		
		String projectDirName = "project";

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			projectDirName = extras.getString(EXTRA_PROJECT_DIR_NAME, "project");
		}

		File projectDir = new File(engineDir, projectDirName);

		core.event(EVENT_CHANGE_ENGINE_DIR, engineDir);
		core.event(EVENT_OPEN_PROJECT, projectDir); 
	}

	private void initializeViews() {
		rootLayout = findViewById(R.id.root_layout);
		gameParent = findViewById(R.id.game_parent);
		
		ScrollView notificationScrollContainer = findViewById(R.id.float_notification_scroll_container);
		LinearLayout notificationContainer = findViewById(R.id.float_notification_container);
		notifications = new FloatNotifications(this, notificationScrollContainer, notificationContainer);
		
		createFloatBubble();
		createFloatWindows();
	}

	private void initializeGame() {
		try {
			editorGame = new EditorGame(this);
			View gameView = initializeForView(editorGame);
			gameParent.addView(gameView);
		} catch (Exception e) {
			error("Error on initialize game: %s", e);
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

		bubble.addAction(EVENT_TOGGLE_EDITOR_MODE, R.drawable.gmd_play_arrow, "Play/Editor Mode");
		bubble.addAction(EVENT_RELOAD_PROJECT, R.drawable.cmd_autorenew, "Reload");
		bubble.addAction(EVENT_OPEN_CODE_EDITOR, R.drawable.gmd_code, "Code editor");
		bubble.addAction(EVENT_OPEN_SCENE_TREE, R.drawable.gmd_photo_size_select_actual, "Scene Tree");
		bubble.addAction(EVENT_OPEN_INSPECTOR, R.drawable.gmd_visibility, "Inspector");
		bubble.addAction(EVENT_EXPORT_PROJECT, R.drawable.gmd_unarchive, "Export APK");
		bubble.addAction(EVENT_INSTALL_PROJECT, R.drawable.gmd_adb, "Install APK");
	}

	private void createFloatWindows() {
		FloatWindow window = new FloatWindow(this);
		window.load("file:///android_asset/scene-tree/scene-tree.html");
		window.title("Title");
		window.position(0, 0);
		window.width(500);
		window.height(500);
		window.addIntoView(rootLayout);
	}
	
	public void createNotification(int iconResId, String title, String message) {
		notifications.createNotification(iconResId, title, message);
	}
	
	public void clearNotifications() {
		notifications.clear();
	}
}
