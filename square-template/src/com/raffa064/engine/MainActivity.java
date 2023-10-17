package com.raffa064.engine;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.raffa064.engine.core.ProjectConfigs;
import com.square.template.R;

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
		
		try {
			rootLayout = findViewById(R.id.root_layout);
			gameParent = findViewById(R.id.game_parent);

			AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
			
			game = new RuntimeGame() {
				@Override
				public void error(final String message) {
					runOnUiThread(new Runnable() {
							@Override
							public void run() {
								Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
							}
						});
				}
			};

			View gameView = initializeForView(game, cfg);
			gameParent.addView(gameView);
			
			int decodeKey = getApplication().getPackageName().hashCode();
			ProjectConfigs configs = new ProjectConfigs("project", false, false, true, decodeKey);
			game.setConfigs(configs);
		} catch (Exception e) {
			Toast.makeText(this, "Error: "+e, Toast.LENGTH_LONG).show();
		}		
    }
}
