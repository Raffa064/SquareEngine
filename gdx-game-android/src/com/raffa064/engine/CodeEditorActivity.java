package com.raffa064.engine;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import org.json.JSONArray;
import org.json.JSONObject;

public class CodeEditorActivity extends Activity {
	private EditorApplication application;
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
		
		application = (EditorApplication) getApplication();
		androidJSI = application.getAndroidJSI();

		checkPermissions();
	}

	private void checkPermissions() {
		if (getApplicationContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
			getApplicationContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
			init();
		} else {
			requestPermissions(
				new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
				123
			);
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);

		if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
			init();
		} else {
			finish();
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
		webSettings.setUseWideViewPort(true);
		webSettings.setJavaScriptEnabled(true);  

		webView.setWebViewClient(new WebViewClient());
		webView.setAddStatesFromChildren(true);
		
		androidJSI.setFolderPath(getIntent().getExtras().getString("project"));
		webView.addJavascriptInterface(androidJSI, "app");

		webView.loadUrl("file:///android_asset/editor/editor.html");
	}

	@Override
	public void onBackPressed() {
		setResult(RESULT_OK);
		finish();
	}
}
