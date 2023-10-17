package com.raffa064.engine;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.square.template.R;
import android.content.pm.ActivityInfo;
import android.widget.Toast;
import android.widget.TextView;
import java.io.IOException;
import java.io.InputStream;
import android.content.ClipboardManager;

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
		
		int decodeKey = getApplication().getPackageName().hashCode();
		
        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
        game = new RuntimeGame(decodeKey) {
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
		
		rootLayout = findViewById(R.id.root_layout);
		gameParent = findViewById(R.id.game_parent);

		gameParent.addView(initializeForView(game, cfg));		
    }
}
