package com.raffa064.engine.ui;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public abstract class CustomTouchListener implements OnTouchListener {
	private float offsetX, offsetY;

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				offsetX = event.getRawX();
				offsetY = event.getRawY();

				return onDown(event.getRawX(), event.getRawY(), event.getActionIndex());
			case MotionEvent.ACTION_MOVE:
				float dragX = event.getRawX() - offsetX;
				float dragY = event.getRawY() - offsetY;

				boolean move = onMove(event.getRawX(), event.getRawY(), dragX, dragY, event.getActionIndex());

				offsetX = event.getRawX();
				offsetY = event.getRawY();

				return move;
			case MotionEvent.ACTION_UP:
				return onUp(event.getRawX(), event.getRawY(), event.getActionIndex());
		}

		return false;
	}

	public abstract boolean onDown(float x, float y, int touch);
	public abstract boolean onMove(float x, float y, float dragX, float dragY, int touch);
	public abstract boolean onUp(float x, float y, int touch);
}
