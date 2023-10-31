package com.raffa064.engine;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.raffa064.engine.core.ProjectConfigs;
import com.raffa064.engine.environments.Android;
import com.raffa064.engine.environments.runtime.RuntimeGame;
import com.square.template.R;

public class MainActivity extends AndroidApplication implements Android {
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
			
			game = new RuntimeGame(this) {
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

			if (orientationName == key) {
				return value;
			}
		}

		return ActivityInfo.SCREEN_ORIENTATION_SENSOR;
	}
}
