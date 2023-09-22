package com.raffa064.engine;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Display;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import android.widget.Toast;

public class MainActivity extends AndroidApplication implements AndroidInterface {
	private final int OPEN_EDITOR_CODE = 1;
	
	private EditorApplication application;
	private AndroidJSI androidJSI;
	
	public String projectPath = "/storage/emulated/0/SquareEngine/project";
	public DebugGame game;
	public boolean isOpenedEditor;

	private RelativeLayout rootLayout;
	private LinearLayout gameParent;
	private TextView debug;

	private FloatWindow sceneTreeWindow;
	private FloatWindow inspectorWindow;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getActionBar().hide();
		setContentView(R.layout.activity_main);
		
		application = (EditorApplication) getApplication();
		application.initJSI(projectPath);
		androidJSI = application.getAndroidJSI();
		
        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
        game = new DebugGame(this);
		
		rootLayout = findViewById(R.id.root_layout);
		gameParent = findViewById(R.id.game_parent);
		debug = findViewById(R.id.debug);

		gameParent.addView(initializeForView(game, cfg));
		
		sceneTreeWindow = new FloatWindow(this);
		sceneTreeWindow.title("Scene Tree");
		sceneTreeWindow.addIntoView(rootLayout);
		sceneTreeWindow.load("file:///android_asset/scene-tree/scene-tree.html");
		androidJSI.sceneTree = sceneTreeWindow.webview();
		
		inspectorWindow = new FloatWindow(this);
		inspectorWindow.title("Inspector");
		inspectorWindow.addIntoView(rootLayout);
		inspectorWindow.load("file:///android_asset/inspector/inspector.html");
		androidJSI.inspector = inspectorWindow.webview();
		
		applyConfigs();
		
		JNI jni = new JNI();
		
		Toast.makeText(this, "2 ˆ 10"+jni.pow(2, 10), 1).show();
    }

	@Override
	public void openSceneTree() {
		runOnUiThread(new Runnable() {
				@Override
				public void run() {
					sceneTreeWindow.openWindow();
					androidJSI.inspector("select()");
				}
			});
	}
	
	@Override
	public void openInspector() {
		runOnUiThread(new Runnable() {
				@Override
				public void run() {
					inspectorWindow.openWindow();
				}
			});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == OPEN_EDITOR_CODE) {
			game.requestReload(projectPath);
			isOpenedEditor = false;
		}
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		applyConfigs(newConfig);
	}

	private void applyConfigs(Configuration configs) {
		Display display = getDisplay();
		
		final String orientation = configs.orientation == Configuration.ORIENTATION_PORTRAIT ? "portrait" : "landscape";
		
		int w, h, x, y;

		w = (int) Float.parseFloat(androidJSI.getEditorData("scenetree_"+orientation+"_width", "400"));
		h = (int) Float.parseFloat(androidJSI.getEditorData("scenetree_"+orientation+"_height", "500"));
		sceneTreeWindow.width(w);
		sceneTreeWindow.height(h);
		
		x = (int) Float.parseFloat(androidJSI.getEditorData("scenetree_"+orientation+"_x", "0"));
		y = (int) Float.parseFloat(androidJSI.getEditorData("scenetree_"+orientation+"_y", ""+(display.getHeight() - sceneTreeWindow.height())));
		sceneTreeWindow.position(x, y);
		
		sceneTreeWindow.setWindowChangeListener(new FloatWindow.OnChangeWindowListener() {
			@Override
			public void move(int x, int y) {
				androidJSI.setEditorData("scenetree_"+orientation+"_x", ""+x);
				androidJSI.setEditorData("scenetree_"+orientation+"_y", ""+y);
			}

			@Override
			public void resize(int w, int h) {
				androidJSI.setEditorData("scenetree_"+orientation+"_width", ""+w);
				androidJSI.setEditorData("scenetree_"+orientation+"_height", ""+h);
			}
		});
		
		w = (int) Float.parseFloat(androidJSI.getEditorData("inspector_"+orientation+"_width", "400"));
		h = (int) Float.parseFloat(androidJSI.getEditorData("inspector_"+orientation+"_height", "500"));
		inspectorWindow.width(w);
		inspectorWindow.height(h);
		
		x = (int) Float.parseFloat(androidJSI.getEditorData("inspector_"+orientation+"_x", ""+(display.getWidth() - inspectorWindow.width())));
		y = (int) Float.parseFloat(androidJSI.getEditorData("inspector_"+orientation+"_y", ""+(display.getHeight() - inspectorWindow.height())));
		inspectorWindow.position(x, y);
		
		inspectorWindow.setWindowChangeListener(new FloatWindow.OnChangeWindowListener() {
				@Override
				public void move(int x, int y) {
					androidJSI.setEditorData("inspector_"+orientation+"_x", ""+x);
					androidJSI.setEditorData("inspector_"+orientation+"_y", ""+y);
				}

				@Override
				public void resize(int w, int h) {
					androidJSI.setEditorData("inspector_"+orientation+"_width", ""+w);
					androidJSI.setEditorData("inspector_"+orientation+"_height", ""+h);
				}
			});
	}
	
	public void applyConfigs() {
		applyConfigs(getResources().getConfiguration());
	}
	
	@Override
	public void setDebugText(final String text) {
		runOnUiThread(new Runnable() {
				@Override
				public void run() {
					debug.setText(text);
				}
			});
	}

	@Override
	public String getProjectPath() {
		return projectPath;
	}
	
	@Override
	public void openEditor() {
		Intent intent = new Intent(this, CodeEditorActivity.class);
		intent.putExtra("project", projectPath);
		startActivityForResult(intent, OPEN_EDITOR_CODE);
		isOpenedEditor = true;
	}
	
	@Override
	public boolean isOpennedEditor() {
		return isOpenedEditor;
	}
	
	@Override
	public boolean isEditorMode() {
		return sceneTreeWindow.isOpenned() || inspectorWindow.isOpenned();
	}
}
