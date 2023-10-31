package com.raffa064.engine.exporter;

import apk64.FileUtils;
import com.raffa064.engine.environments.runtime.Encryptor;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ProjectSafer {
	public static final String BIG_JS_FILE_NAME = "big.js";
	
	public static void safe(File projectDir, int decodeKey) throws Exception {
		List<File> scriptFiles = new ArrayList<>();
		ProjectUtils.scanJSFiles(projectDir, scriptFiles); 
		
		String bigFileContent = "";
		for (File jsFile : scriptFiles) {
			String content = FileUtils.readFileString(jsFile);
			FileUtils.deleteFiles(jsFile); // Delete file
			
			bigFileContent += "\n" + content; // Append file content to big.js content
		}
		
		String encryptedContent = Encryptor.encrypt(bigFileContent, decodeKey);
		
		File bigFile = new File(projectDir, BIG_JS_FILE_NAME);
		FileUtils.writeFile(bigFile, encryptedContent);
	}
}
