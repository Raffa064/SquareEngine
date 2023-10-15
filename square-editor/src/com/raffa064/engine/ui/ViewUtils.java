package com.raffa064.engine.ui;

import android.view.View;

public class ViewUtils {
    public static void toggleVisibility(View view) {
		if (view.getVisibility() == View.GONE) {
			view.setVisibility(View.VISIBLE);
			
			return;
		}

		view.setVisibility(View.GONE);
	}
}
