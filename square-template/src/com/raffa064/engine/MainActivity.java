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
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.ClipboardManager;

public class MainActivity extends AndroidApplication implements Android {
	public RuntimeGame game;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getActionBar().hide();

		try {
			AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();

			game = new RuntimeGame(this);

			View gameView = initializeForView(game, cfg);
			setContentView(gameView);

			int decodeKey = getApplication().getPackageName().hashCode();
			ProjectConfigs configs = new ProjectConfigs("project", false, false, true, decodeKey);
			game.setConfigs(configs);
		} catch (Exception e) {
			error("Sorry, an error occurred while initializing the game.", e);
		}		
    }

	@Override
	public void debug(String message) {
		// Ignored for runtime game
	}

	@Override
	public void warning(String warning) {
		// Ignored for runtime game
	}

	@Override
	public void error(String message, final Throwable error) {
		game.stop();

		runOnUiThread(new Runnable() {
				@Override
				public void run() {
					new AlertDialog.Builder(MainActivity.this)
						.setTitle("Error")
						.setMessage("Unffortunelly, the game has crashed.")
						.setNegativeButton("Close Game", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface p1, int p2) {
								finish();
							}
						})
						.setPositiveButton("Copy logs", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface p1, int p2) {
								ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

								String msg = "Error Message: " + error;

								for (StackTraceElement  ste : error.getStackTrace()) {
									msg += "\n" + ste;
								}

								cm.setText(msg);
								Toast.makeText(MainActivity.this, "Error copied!", Toast.LENGTH_LONG).show();
								
								finish();
							}
						})
						.setCancelable(false)
						.create()
						.show();
				}
			});
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

			if (orientationName.equals(key)) {
				return value;
			}
		}

		return ActivityInfo.SCREEN_ORIENTATION_SENSOR;
	}
}
