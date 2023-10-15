package com.raffa064.engine.ui;

import android.view.View;
import android.widget.LinearLayout.LayoutParams;

public class ResizerListener extends CustomTouchListener {
	public static final int MODE_HORIZONTAL = 1;
	public static final int MODE_VERTICAL = 2;

	private View view;
	private LayoutParams params;
	private int mode;

	public ResizerListener(View view, int mode) {
		this.view = view;
		this.params = (LayoutParams) view.getLayoutParams();
		this.mode = mode;
	}

	@Override
	public boolean onDown(float x, float y, int touch) {
		return true;
	}

	@Override
	public boolean onMove(float x, float y, float dragX, float dragY, int touch) {

		params.width += dragX * (mode & 0b1);
		params.height += dragY * ((mode >> 1) & 0b1);

		int screenWidth = ((View) view.getParent()).getWidth();
		int screenHeight = ((View) view.getParent()).getHeight();

		params.width = Math.max(view.getMinimumWidth(), Math.min(screenWidth, params.width));
		params.height = Math.max(view.getMinimumHeight(), Math.min(screenHeight, params.height));

		view.setLayoutParams(params);

		return true;
	}

	@Override
	public boolean onUp(float x, float y, int touch) {
		return true;
	}
}
