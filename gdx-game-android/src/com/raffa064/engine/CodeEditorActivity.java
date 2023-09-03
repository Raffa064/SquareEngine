package com.raffa064.engine;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;


public class CodeEditorActivity extends Activity {
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

		if (getApplicationContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
			getApplicationContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
			init();
		} else {
			// Solicite permissões de leitura e escrita
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

		// Configurar as configurações do WebView
		WebSettings webSettings = webView.getSettings();
		webSettings.setUseWideViewPort(true);
		webSettings.setJavaScriptEnabled(true);  // Habilitar JavaScript, se necessário


		// Configurar o cliente do WebView
		webView.setWebViewClient(new WebViewClient());

		webView.setAddStatesFromChildren(true);

		Android android = new Android();
		android.setFolderPath(getIntent().getExtras().getString("project"));
		webView.addJavascriptInterface(android, "app");

		// Carregar o arquivo HTML da pasta "assets"
		webView.loadUrl("file:///android_asset/editor.html");
	}

	public static class Android {
		public String projectFolder;

		@JavascriptInterface		
		public void setFolderPath(String path) {
			projectFolder = path;
		}

		@JavascriptInterface
		public String getFolderPath() {
			return projectFolder;
		}

		@JavascriptInterface		
		public String getFolderContent(String path) throws Exception {
			File file = new File(path);
			String toString = convertFolderToJson(file).toString();
			return toString;
		}

		@JavascriptInterface		
		public String getFileContent(String path) throws Exception {
			File file = new File(path);
			
			if (!file.exists()) return null;

			FileInputStream fis = new FileInputStream(file);
			byte[] buffer = new byte[fis.available()];
			fis.read(buffer);
			fis.close();

			String content = new String(buffer);
			return content;
		}

		@JavascriptInterface		
		public void writeFileContent(String path, String content) throws Exception {
			File file = new File(path);

			FileOutputStream fos = new FileOutputStream(file);
			fos.write(content.getBytes());
			fos.flush();
			fos.close();
		}

		@JavascriptInterface		
		public String getEditorData(String key, String defaultValue) {
			try {
				String fileContent = getFileContent(projectFolder + "/.editor");

				JSONObject editorData = new JSONObject(fileContent);

				if (editorData.has(key)) {
					return editorData.getString(key);	
				}
			} catch (Exception e) {}
			
			return defaultValue;
		}
		
		@JavascriptInterface		
		public void setEditorData(String key, String value) {
			try {
				String path = projectFolder + "/.editor";
				String fileContent = getFileContent(path);
				
				if (fileContent == null) {
					fileContent = "{}";
				}

				JSONObject editorData = new JSONObject(fileContent);
				editorData.putOpt(key, value);

				writeFileContent(path, editorData.toString());
			} catch (Exception e) {}
		}

		public JSONObject convertFolderToJson(File folder) throws Exception {
			JSONObject folderJson = new JSONObject();
			folderJson.putOpt("name", folder.getName());
			folderJson.putOpt("path", folder.getAbsolutePath());
			folderJson.putOpt("isDirectory", folder.isDirectory());

			if (folder.isDirectory()) {
				JSONArray childrenArray = new JSONArray();
				File[] files = folder.listFiles();

				if (files != null) {
					for (File file : files) {
						childrenArray.put(convertFolderToJson(file));
					}
				}

				folderJson.putOpt("children", childrenArray);
			} else {
				String extension = getFileExtension(folder);
				folderJson.putOpt("extension", extension);
			}

			return folderJson;
		}

		public static String getFileExtension(File file) {
			String name = file.getName();
			int lastDotIndex = name.lastIndexOf('.');
			if (lastDotIndex > 0) {
				return name.substring(lastDotIndex + 1);
			}
			return "";
		}
	}

	@Override
	public void onBackPressed() {
		setResult(RESULT_OK);
		finish();
	}

	public static class MyCustomInputConnection extends BaseInputConnection {

		public MyCustomInputConnection(View targetView, boolean fullEditor) {
			super(targetView, fullEditor);
		}

		@Override
		public boolean deleteSurroundingText(int beforeLength, int afterLength) {       
			// magic: in latest Android, deleteSurroundingText(1, 0) will be called for backspace
			if (beforeLength == 1 && afterLength == 0) {
				// backspace
				return super.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
					&& super.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL));
			}

			return super.deleteSurroundingText(beforeLength, afterLength);
		}
	}
}
