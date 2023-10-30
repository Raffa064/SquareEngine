package com.raffa064.engine.exporter;

import apk64.FileUtils;
import com.raffa064.engine.environments.runtime.Encryptor;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ProjectSafer {
	public static void safe(File projectDir, int decodeKey) throws Exception {
		List<File> scriptFiles = new ArrayList<>();
		ProjectUtils.scanFiles(projectDir, ".js", scriptFiles); 
		
		String bigFileContent = "";
		for (File jsFile : scriptFiles) {
			String content = FileUtils.readFileString(jsFile);
			FileUtils.deleteFiles(jsFile);
			
			bigFileContent += "\n"+content;
		}
		
		String encryptedContent = Encryptor.encrypt(bigFileContent, decodeKey);
		
		File bigFile = new File(projectDir, "big.js");
		FileUtils.writeFile(bigFile, encryptedContent);
	}
}
