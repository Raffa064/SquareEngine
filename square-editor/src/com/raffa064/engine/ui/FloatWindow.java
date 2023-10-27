package com.raffa064.engine.ui;

import android.animation.Animator;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import com.raffa064.engine.R;

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
	private OnChangeWindowListener windowChangeListener;

	public FloatWindow(Activity activity) {
		this.activity = activity;

		inflate(activity.getLayoutInflater(), 400, 500);
	}

	public WebView webview() {
		return windowWebView;
	}

	public void setWindowChangeListener(OnChangeWindowListener windowChangeListener) {
		this.windowChangeListener = windowChangeListener;
	}

	public OnChangeWindowListener getWindowChangeListener() {
		return windowChangeListener;
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

		windowTopBar.setOnTouchListener(new HoverListener(window, false) {
				@Override
				public boolean onMove(float x, float y, float dragX, float dragY, int touch) {
					boolean onMove = super.onMove(x, y, dragX, dragY, touch);
					
					if (windowChangeListener != null) {
						windowChangeListener.move(x(), y());
					}
					
					return onMove;
				}
			});
			
		windowResizerRight.setOnTouchListener(new ResizerListener(window, ResizerListener.MODE_HORIZONTAL) {
				@Override
				public boolean onMove(float x, float y, float dragX, float dragY, int touch) {
					boolean onMove = super.onMove(x, y, dragX, dragY, touch);

					if (windowChangeListener != null) {
						windowChangeListener.resize(width(), height());
					}

					return onMove;
				}
			});
			
		windowResizerBottom.setOnTouchListener(new ResizerListener(window, ResizerListener.MODE_VERTICAL) {
				@Override
				public boolean onMove(float x, float y, float dragX, float dragY, int touch) {
					boolean onMove = super.onMove(x, y, dragX, dragY, touch);

					if (windowChangeListener != null) {
						windowChangeListener.resize(width(), height());
					}

					return onMove;
				}
			});

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
	}

	public void load(String url) {
		windowWebView.loadUrl(url);
	} 

	public void addIntoView(ViewGroup view) {
		view.addView(window);	
	}

	public void position(int x, int y) {
		windowParams.setMargins(x, y, 0, 0);
		window.setLayoutParams(windowParams);
	}

	public int x() {
		return windowParams.leftMargin;	
	}

	public int y() {
		return windowParams.topMargin;	
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
	
	public interface OnChangeWindowListener {
		public void move(int x, int y);

		public void resize(int w, int h);
	}
}
