package com.raffa064.engine;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import com.raffa064.engine.R;
import android.widget.ImageButton;
import android.webkit.WebView;
import android.view.View.OnTouchListener;
import android.view.MotionEvent;
import android.view.Display;
import android.view.View.OnClickListener;
import android.animation.Animator;
import android.view.animation.Animation;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;

public class FloatWindow {
	private Activity activity;
	private View window;
	private LayoutParams windowParams;
	private RelativeLayout windowTopBar;
	private TextView windowTitle;
	private ImageButton windowClose;
	private WebView windowWebView;
	private View windowResizerRight;
	private View windowResizerBottom;

	public FloatWindow(Activity activity) {
		this.activity = activity;

		inflate(activity.getLayoutInflater(), 400, 500);
	}

	private void inflate(LayoutInflater inflater, int width, int height) {
		window = inflater.inflate(R.layout.float_window, null);
		windowTopBar = window.findViewById(R.id.float_window_top_bar);
		windowTitle = window.findViewById(R.id.float_window_title);
		windowClose = window.findViewById(R.id.float_window_close);
		windowWebView = window.findViewById(R.id.float_window_webview);	
		windowResizerRight = window.findViewById(R.id.float_window_resizer_right);	
		windowResizerBottom = window.findViewById(R.id.float_window_resizer_bottom);
		
		window.setVisibility(View.GONE);
		
		windowParams = new LayoutParams(width, height);
		window.setLayoutParams(windowParams);
		
		windowTopBar.setOnTouchListener(new HoverListener(window));
		windowResizerRight.setOnTouchListener(new ResizerListener(window, ResizerListener.MODE_HORIZONTAL));
		windowResizerBottom.setOnTouchListener(new ResizerListener(window, ResizerListener.MODE_VERTICAL));
		
		windowClose.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					closeWindow();
				}			
		});
		
		// Configurar as configurações do WebView
		WebSettings webSettings = windowWebView.getSettings();
		webSettings.setUseWideViewPort(true);
		webSettings.setJavaScriptEnabled(true);  // Habilitar JavaScript, se necessário

		// Configurar o cliente do WebView
		windowWebView.setWebViewClient(new WebViewClient());

		windowWebView.setAddStatesFromChildren(true);
		
		windowWebView.loadUrl("file:///android_asset/scene-tree/scene-tree.html");
	} 

	public void addIntoView(ViewGroup view) {
		view.addView(window);	
	}
	
	public void position(int x, int y) {
		windowParams.setMargins(x, y, 0, 0);
		window.setLayoutParams(windowParams);
	}
	
	public int width() {
		return windowParams.width;
	}

	public void width(int width) {
		windowParams.width = width;
	}

	public int height() {
		return windowParams.height;
	}

	public void height(int height) {
		windowParams.height = height;
	}

	public String title() {
		return windowTitle.getText().toString();
	}

	public void title(String title) {
		windowTitle.setText(title);
	}

	public void openWindow() {
		window.setAlpha(0);
		window.setVisibility(View.VISIBLE);
		window
			.animate()
			.alpha(.86f)
			.setDuration(280)
			.setListener(null)
			.start();
	}

	public void closeWindow() {
		window
			.animate()
			.alpha(0)
			.setDuration(280)
			.setListener(new Animator.AnimatorListener() {

				@Override
				public void onAnimationStart(Animator p1) {
				}

				@Override
				public void onAnimationEnd(Animator p1) {
					window.setVisibility(View.GONE);
				}

				@Override
				public void onAnimationCancel(Animator p1) {
				}

				@Override
				public void onAnimationRepeat(Animator p1) {
				}
			})
			.start();
	}
	
	public void toggleWindow() {
		if (window.getVisibility() == View.GONE) {
			openWindow();
		} else {
			closeWindow();
		}
	}
	
	public boolean isOpenned() {
		return window.getVisibility() == View.VISIBLE;
	}
	
	public static abstract class CustomTouchListener implements OnTouchListener {
		private float offsetX, offsetY;
		
		@Override
		public boolean onTouch(View view, MotionEvent event) {
			switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					offsetX = event.getRawX();
					offsetY = event.getRawY();
					
					return onUp(event.getRawX(), event.getRawY(), event.getActionIndex());
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

	public static class HoverListener extends CustomTouchListener {
		private View view;
		private LayoutParams params;
		
		public HoverListener(View view) {
			this.view = view;
			this.params = (LayoutParams) view.getLayoutParams();
		}
		
		@Override
		public boolean onDown(float x, float y, int touch) {
			return true;
		}

		@Override
		public boolean onMove(float x, float y, float dragX, float dragY, int touch) {
			int iDragY = (int) dragY;
			int iDragX = (int) dragX;

			int px = params.leftMargin + iDragX;
			int py = params.topMargin + iDragY;

			int screenWidth = ((View) view.getParent()).getWidth();
			int screenHeight = ((View) view.getParent()).getHeight();

			px = Math.max(0, Math.min(screenWidth - params.width, px));
			py = Math.max(0, Math.min(screenHeight - params.height, py));
			
			params.setMargins(px, py, 0, 0);
			
			view.setLayoutParams(params);
			
			return true;
		}
		
		@Override
		public boolean onUp(float x, float y, int touch) {
			return true;
		}
	}
	
	public static class ResizerListener extends CustomTouchListener {
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
}
