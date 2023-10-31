package com.raffa064.engine.exporter;

import apk64.FileUtils;
import com.raffa064.engine.core.ScriptEngine;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;

public class ProjectOptimizer {
	public static void optimizeScripts(File projectDir) throws Exception {
		List<File> scriptFiles = new ArrayList<>();
		ProjectUtils.scanJSFiles(projectDir, scriptFiles); // Get script files
		
		ScriptEngine engine = new ScriptEngine();

		for (File jsFile : scriptFiles) {
			String source = FileUtils.readFileString(jsFile);
			String optimizedSource = engine.transpile(source); // Pre-transpile js64

			FileUtils.writeFile(jsFile, optimizedSource);
		}
		
		engine.exit();
	}
}
