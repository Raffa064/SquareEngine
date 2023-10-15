package com.raffa064.engine.ui;

import android.view.View;
import android.widget.RelativeLayout.LayoutParams;

public class HoverListener extends CustomTouchListener {
	private View view;
	private LayoutParams params;
	private boolean isDrag;

	public HoverListener(View view) {
		this.view = view;
		this.params = (LayoutParams) view.getLayoutParams();
	}

	@Override
	public boolean onDown(float x, float y, int touch) {
		isDrag = false;
		return false;
	}

	@Override
	public boolean onMove(float x, float y, float dragX, float dragY, int touch) {
		int iDragX = (int) dragX;
		int iDragY = (int) dragY;
		
		if (isDrag || Math.abs(iDragX) > 1 || Math.abs(iDragY) > 1) {
			int px = params.leftMargin + iDragX;
			int py = params.topMargin + iDragY;

			int screenWidth = ((View) view.getParent()).getWidth();
			int screenHeight = ((View) view.getParent()).getHeight();

			px = Math.max(0, Math.min(screenWidth - view.getWidth(), px));
			py = Math.max(0, Math.min(screenHeight - view.getHeight(), py));

			params.setMargins(px, py, 0, 0);

			view.setLayoutParams(params);
			isDrag = true;
			
			return true;
		}

		return false;
	}

	@Override
	public boolean onUp(float x, float y, int touch) {
		return isDrag;
	}
}
