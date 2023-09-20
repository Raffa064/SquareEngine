package com.raffa064.engine;

import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

public class MainActivity extends AndroidApplication implements AndroidInterface {
	private final int OPEN_EDITOR_CODE = 1;
	
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
		
        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
        game = new DebugGame(this);
		
		rootLayout = findViewById(R.id.root_layout);
		gameParent = findViewById(R.id.game_parent);
		debug = findViewById(R.id.debug);

		gameParent.addView(initializeForView(game, cfg));
		
		Display display = getDisplay();

		sceneTreeWindow = new FloatWindow(this);
		sceneTreeWindow.title("Scene Tree");
		sceneTreeWindow.addIntoView(rootLayout);
		sceneTreeWindow.position(
			0, 
			display.getHeight() - sceneTreeWindow.height()
		);
		
		inspectorWindow = new FloatWindow(this);
		inspectorWindow.title("Inspector");
		inspectorWindow.addIntoView(rootLayout);
		inspectorWindow.position(
			display.getWidth() - inspectorWindow.width(),
			display.getHeight() - inspectorWindow.height()
		);
    }

	@Override
	public void openSceneTree() {
		runOnUiThread(new Runnable() {
				@Override
				public void run() {
					sceneTreeWindow.openWindow();
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
