package com.raffa064.engine.exporter;

import java.io.File;
import java.util.List;

public class ProjectUtils {
	public static void scanFiles(File file, String extension, List<File> files) {
		if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				scanFiles(f, extension, files);
			}

			return;
		}

		if (file.getName().endsWith(extension)) {
			files.add(file);
		}
	}
}
