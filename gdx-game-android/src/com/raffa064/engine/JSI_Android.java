package com.raffa064.engine;

import android.webkit.JavascriptInterface;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import org.json.JSONArray;
import org.json.JSONObject;

public class JSI_Android {
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

	@JavascriptInterface		
	public void createFile(String path) throws IOException {
		File file = new File(path);
		file.getParentFile().mkdirs();
		file.createNewFile();
	}

	@JavascriptInterface		
	public void createFolder(String path) throws IOException {
		File folder = new File(path);
		folder.mkdirs();
	}

	@JavascriptInterface		
	public void renameFile(String path, String name) throws IOException {
		File file = new File(path);
		String  tmpPrefix = "tmp_" + (System.currentTimeMillis() / 1000);

		File renamedTmpFile = new File(file.getParentFile(), tmpPrefix + name);
		file.renameTo(renamedTmpFile);

		File renamedFile = new File(file.getParentFile(), name);
		renamedTmpFile.renameTo(renamedFile);
	}

	@JavascriptInterface		
	public void deleteFile(String path) throws IOException {
		File file = new File(path);
		file.delete();
	}

	@JavascriptInterface		
	public boolean existsFile(String path) throws Exception {
		File file = new File(path);
		return file.exists();
	}

	public JSONObject convertFolderToJson(File folder) throws Exception {
		JSONObject folderJson = new JSONObject();
		folderJson.putOpt("name", folder.getName());
		folderJson.putOpt("path", folder.getAbsolutePath());
		folderJson.putOpt("isDirectory", folder.isDirectory());

		if (folder.isDirectory()) {
			JSONArray childrenArray = new JSONArray();
			File[] files = folder.listFiles();

			Arrays.sort(files, new Comparator<File>() {
					@Override
					public int compare(File fileA, File fileB) {
						if (fileA.isDirectory() != fileB.isDirectory()) {
							if (fileA.isDirectory()) {
								return -1; // A is placed after B
							}

							return 1; // A is placed before B
						}

						return fileA.getName().compareToIgnoreCase(fileB.getName()); // Place by name a-Z
					}
				});

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
