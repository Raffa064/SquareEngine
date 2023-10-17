package com.raffa064.engine.core.tests;

import com.raffa064.engine.core.App;
import com.raffa064.engine.core.GameObject;
import com.raffa064.engine.core.JSONLoader;
import com.raffa064.engine.core.Scene;
import com.raffa064.engine.core.ScriptEngine;
import com.raffa064.engine.core.api.ComponentAPI;
import com.raffa064.engine.core.api.LoggerAPI;
import com.raffa064.engine.core.components.commons2d.Transform2D;
import java.io.File;
import java.io.FileInputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import com.raffa064.engine.Encryptor;

public class Main {
	public static void main(String[] args) throws Exception {
		String source = readFile("/storage/emulated/0/AppProjects/SquareEngine/gdx-game/src/com/raffa064/engine/core/tests/teste.js");
		
		int key = (int) (Math.random() * Integer.MAX_VALUE);
		
		String encrypt = Encryptor.encrypt(source, key);
		String decrypt = Encryptor.decrypt(encrypt, key);

		System.out.println(encrypt);
		System.out.println();
		System.out.println(decrypt);
	}

	private static void test() {
		String source = readFile("/storage/emulated/0/AppProjects/SquareEngine/gdx-game/src/com/raffa064/engine/core/tests/teste.js");
		
		Strings strings = new Strings();
		
		source = removeInlineComments(source);
		source = removeMultlineComments(source);
		source = extractStrings(source, strings);

		source = strings.toHeader() + source;
		
		System.out.println(source);
		
		ScriptEngine engine = new ScriptEngine();
		engine.inject("logger", new LoggerAPI(null));
		engine.compile(source);
	}

	private static String removeInlineComments(String source) {
		Pattern compile = Pattern.compile("//.*\n"); 
		Matcher matcher = compile.matcher(source);

		while (matcher.find()) {
			int start = matcher.start();
			int end = matcher.end();

			source = source.substring(0, start) + source.substring(end - 1, source.length());
		}
		
		return source;
	}

	private static String removeMultlineComments(String source) {
		Pattern compile = Pattern.compile("/\\*.*\\*/", Pattern.DOTALL); 
		Matcher matcher = compile.matcher(source);

		while (matcher.find()) {
			int start = matcher.start();
			int end = matcher.end();

			source = source.substring(0, start) + source.substring(end, source.length());
		}

		return source;
	}
	
	public static String extractStrings(String source, Strings strings) {
		boolean isString = false;
		int stringStart = 0;
		char stringQuote = '\0';
		
		for (int i = 0; i < source.length(); i++) {
			char lastCharAt = source.charAt(Math.max(0, i-1));
			char charAt = source.charAt(i);
			
			if (charAt == '\'' || charAt == '"') {
				if (isString) {
					if (charAt == stringQuote && lastCharAt != '\\') {
						isString = false;
						
						String str = source.substring(stringStart, i+1);
						
						String constStringName = strings.add(str);
						
						source = source.substring(0, stringStart) + constStringName + source.substring(i+1, source.length());
						i = stringStart;
					}
				} else {
					isString = true;
					stringStart = i;
					stringQuote = charAt;
				}
			}	
		}

		return source;
	}
	
	public static class Strings {
		private final String AUTO_CONST_STRING = "AUTO_CONST_STRING";
		private HashMap<Integer, String> strings = new HashMap<>();
		private int _id;
		
		public String add(String str) {
			int id = _id++;
			
			strings.put(id, str);
			
			return AUTO_CONST_STRING + "_" + id;
		}
		
		public String toHeader() {
			String header = "";
			
			for (Map.Entry<Integer, String> entry : strings.entrySet()) {
				int id = entry.getKey();
				String str = entry.getValue();
				
				header += "const " + AUTO_CONST_STRING + "_" + id + " = " + str + "; \n";
			}
			
			return header;
		}
	}
	
	private static void test_transpiler2() {
		ScriptEngine engine = new ScriptEngine();
		
		engine.inject("logger", new LoggerAPI(null));
		
		String script = readFile("/storage/emulated/0/AppProjects/SquareEngine/gdx-game/src/com/raffa064/engine/core/tests/teste.js");
		String transpiledScript = engine.transpile(script);
		System.out.println(transpiledScript);
		System.out.println(transpiledScript.split("\n").length + " <- " + script.split("\n").length);
		
		engine.compile(script);
	}

	private static void test_name_counter() {
		String name = "test";

		Matcher matcher = Pattern.compile("\\([0-9]+\\)").matcher(name);

		if (matcher.find()) {
			int start = matcher.start();
			int end = matcher.end();

			int counter = Integer.parseInt(name.substring(start + 1, end - 1)) + 1;
			name = name.substring(0, start) + "(" + counter + ")" + name.substring(end, name.length());
		} else {
			name = name + " (1)";
		}

		System.out.println("COUNTER: " + name);
	}

	private static void test_transpiler() {
		ScriptEngine engine = new ScriptEngine();
		String script = readFile("/storage/emulated/0/AppProjects/SquareEngine/gdx-game/src/com/raffa064/engine/core/tests/teste.js"); //"/storage/emulated/0/AppProjects/GameEngine/gdx-game/src/com/raffa064/engine/core/tests/teste.js");
		String transpile = engine.transpile(script);
		System.out.println(transpile);

		
	}

	private static void test_SceneLoader() throws Exception {
		Scene scene = new Scene();
		scene.setName("Scene");

		GameObject gameObject = new GameObject();
		gameObject.setName("Objecto1");
		Transform2D trans = new Transform2D();
		gameObject.add(trans);
		scene.addChild(gameObject);

		GameObject gameObject2 = new GameObject();
		gameObject2.setName("Objecto2");
		Transform2D trans2 = new Transform2D();
		gameObject2.add(trans2);
		scene.addChild(gameObject2);

		App app = new App();
		app.Component = new ComponentAPI(app);
		JSONLoader sceneLoader = new JSONLoader(app);
		String json = sceneLoader.asJson(scene);
		Scene loaded = sceneLoader.sceneFromJson(json);
		String json2 = sceneLoader.asJson(loaded, 10);
		System.out.println(json2);
	}

	public static String readFile(String path) {
		File file = new File(path);
		try {
			FileInputStream fis = new FileInputStream(file);
			byte[] buffer = new byte[fis.available()];
			while (fis.read(buffer) > 0);
			fis.close();
			return new String(buffer);
		} catch (Exception e) {
			return null;
		}
	}

}
