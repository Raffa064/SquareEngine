package com.raffa064.engine;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.raffa064.engine.ui.MyCustomInputConnection;
import android.widget.Toast;
import android.view.WindowManager;

public class CodeActivity extends Activity {
	public static final String EXTRA_PROJECT_PATH = "projectPath";
	public static final int REQUEST_PERMISSIONS_CODE = 1;
	
	private AndroidJSI androidJSI;	
	private WebView webView;

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		webView.saveState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		webView.restoreState(savedInstanceState);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getWindow().setFlags(
			WindowManager.LayoutParams.FLAG_FULLSCREEN,
			WindowManager.LayoutParams.FLAG_FULLSCREEN
		);

		androidJSI = new AndroidJSI();
		checkPermissions();
	}

	private void checkPermissions() {
		if (getApplicationContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
			getApplicationContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
			init();
		} else {
			requestPermissions(
				new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
				REQUEST_PERMISSIONS_CODE
			);
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);

		if (requestCode == REQUEST_PERMISSIONS_CODE) {
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				init();
			} else {
				Toast.makeText(this, "Permissions required!", Toast.LENGTH_SHORT).show();
				finish();
			}
		}
	}

	private void init() {
		if (getActionBar() != null) {
			getActionBar().hide();
		}

		webView = new WebView(this) {
			@Override
			public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
				MyCustomInputConnection myCustomInputConnection = new MyCustomInputConnection(webView, true);
				return myCustomInputConnection;
			}
		};
		
		setContentView(webView);

		WebSettings webSettings = webView.getSettings();
//		webSettings.setUseWideViewPort(true);
		webSettings.setJavaScriptEnabled(true);  

		webView.setWebViewClient(new WebViewClient());
		webView.setAddStatesFromChildren(true);
		
		String projectPath = getIntent().getExtras().getString(EXTRA_PROJECT_PATH);
		androidJSI.setFolderPath(projectPath);
		webView.addJavascriptInterface(androidJSI, AndroidJSI.INTERFACE_NAME);

		webView.loadUrl("file:///android_asset/code-editor/editor.html");
	}

	@Override
	public void onBackPressed() {
		setResult(RESULT_OK);
		finish();
	}
}
