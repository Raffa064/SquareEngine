package com.raffa064.engine;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

public class MainActivity extends AndroidApplication implements AndroidInterface {
	private final int OPEN_EDITOR_CODE = 1;
	
	public String projectPath = "/storage/emulated/0/SquareEngine/project";
	public DebugGame game;
	public boolean isOpenedEditor;
	
	private LinearLayout gameParent;
	private TextView debug;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getActionBar().hide();
		setContentView(R.layout.activity_main);
		
        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
        game = new DebugGame(this);
		
		gameParent = findViewById(R.id.game_parent);
		debug = findViewById(R.id.debug);

		gameParent.addView(initializeForView(game, cfg));

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
}
