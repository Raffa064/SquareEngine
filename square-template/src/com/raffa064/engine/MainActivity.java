package com.raffa064.engine;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.square.template.R;
import android.content.pm.ActivityInfo;

public class MainActivity extends AndroidApplication {
	public RuntimeGame game;

	private RelativeLayout rootLayout;
	private LinearLayout gameParent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getActionBar().hide();
		setContentView(R.layout.activity_main);

        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
        game = new RuntimeGame();
		
		rootLayout = findViewById(R.id.root_layout);
		gameParent = findViewById(R.id.game_parent);

		gameParent.addView(initializeForView(game, cfg));		
    }
}
