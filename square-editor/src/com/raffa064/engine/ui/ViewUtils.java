package com.raffa064.engine.ui;

import android.view.View;

public class ViewUtils {
	public static void gone(View view) {
		view.setVisibility(View.GONE);
	}
	
	public static void invisible(View view) {
		view.setVisibility(View.INVISIBLE);
	} 
	
	public static void visible(View view) {
		view.setVisibility(View.VISIBLE);
	} 

	public static boolean isGone(View view) {
		return view.getVisibility() == View.GONE;
	}
	
	public static boolean isInvisible(View view) {
		return view.getVisibility() == View.INVISIBLE;
	}
	
	public static boolean isVisible(View view) {
		return view.getVisibility() == View.VISIBLE;
	}
	
    public static void toggleVisibility(View view) {
		if (isGone(view)) {
			visible(view);
			return;
		}

		gone(view);
	}
}
