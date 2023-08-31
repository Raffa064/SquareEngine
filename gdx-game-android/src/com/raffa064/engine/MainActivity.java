package com.raffa064.engine;

import android.os.Bundle;
import android.widget.LinearLayout;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import android.view.WindowManager;

public class MainActivity extends AndroidApplication {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getActionBar().hide();
        setContentView(R.layout.activity_main);
        LinearLayout game_parent = (LinearLayout) findViewById(R.id.game_parent);
        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
		//configure your libgdx project hare
		//configure seu projeto libgdx aqui
        game_parent.addView(initializeForView(new MyGdxGame(), cfg));
    }
}
