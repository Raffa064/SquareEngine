package com.raffa064.engine.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import java.lang.reflect.InvocationTargetException;
import org.mozilla.javascript.ErrorReporter;

public class ScriptEngine {
    public Context ctx;
	public ScriptableObject globalScope;
	public int globalConstStringId = 0;

	public ScriptEngine() {
		init();
	}

	public void init() {
		ctx = Context.enter();
		ctx.setOptimizationLevel(-1);
		globalScope = ctx.initStandardObjects();
	}
	
	public void setErrorReporter(ErrorReporter errorReporter) {
		ctx.setErrorReporter(errorReporter);
	}

	public void compile(String script, String name) {
		ctx.compileString(transpile(script), name, 1, null).exec(ctx, globalScope);
	}
	
	public void compile(String script) {
		compile(script, "compiled.js");
	}

	public CompiledScript newObject(Scriptable scope, String name) {
		Scriptable objectScope = ctx.newObject(scope, name);

		if (!ScriptableObject.hasProperty(objectScope, "name")) {
			ScriptableObject.putProperty(objectScope, "name", name);
		}

		CompiledScript compiledScript = new CompiledScript(objectScope);
		return compiledScript;
	}

	public CompiledScript newObject(String name) {
		return newObject(globalScope, name);
	}

	public Scriptable newScope() {
		return ctx.newObject(globalScope);
	}

	public void inject(Scriptable scope, String name, Object api) {
		ScriptableObject.putProperty(scope, name, api);
	}
	
	public ScriptEngine injectClass(Class clazz) {
		String code = "const " + clazz.getSimpleName() + " = "+clazz.getName()+"";
		compile(code);
		return this;
	}

	public ScriptEngine inject(String name, Object api) {
		inject(globalScope, name, api);
		return this;
	}

	public String transpile(String js64) {
		js64 = transpile_classDefinition(js64);
		js64 = transpile_methodSintax(js64);
		js64 = transpile_referenceSintax(js64);
		js64 = transpile_sharpCommentsSintax(js64);
		return js64;
	}

	private String transpile_classDefinition(String js64) {
		List<String> classList = new ArrayList<>();

		Pattern compile = Pattern.compile("[A-z0-9_\\$]+\\s*::\\s*[A-z0-9_\\$]+");
		Matcher matcher = compile.matcher(js64);
		while (matcher.find()) {
			int start = matcher.start();
			String className = js64.substring(start, js64.indexOf("::", start)).trim();

			if (!classList.contains(className)) {
				classList.add(className);
			}
		}

		for (String clazz : classList) {
			js64 = "function " + clazz + "() {%"+clazz+"%}; "+clazz+".prototype.name = '"+clazz+"'; "+clazz+".prototype.exports = {}; const _"+clazz+" = '"+clazz+"'; "  + js64;
		}
		
		js64 = transpile_exportSintax(js64);

		return js64;
	}
	
	private String transpile_exportSintax(String js64) {
		HashMap<String, List<String>> exportedProps = new HashMap<>();
		
		Pattern compile = Pattern.compile("export\\s+[A-z0-9_\\$]+\\s*::\\s*[A-z0-9_\\$]+\\s*=\\s*(STRING|COLOR|INTEGER|FLOAT|VECTOR2|GAME_OBJECT)");
		Matcher matcher;
		while ((matcher = compile.matcher(js64)).find()) {
			int start = matcher.start();
			int end = matcher.end();

			String className = js64.substring(start + "export".length(), js64.indexOf("::", start)).trim();
			String propName = js64.substring(js64.indexOf("::", start) + 2, js64.indexOf("=", start)).trim();
			String propType = js64.substring(js64.indexOf("=", start) + 1, end).trim();
			
			List<String> classProps = exportedProps.getOrDefault(className, new ArrayList<String>());
			classProps.add(propName);
			classProps.add(propType);
			exportedProps.put(className, classProps);
			
			js64 = js64.substring(0, start) + "// export " + className + "." + propName +  js64.substring(end, js64.length());
		}
		
		js64 = transpile_defaultExportValues(js64, exportedProps);
		
		return js64;
	}
	
	private String defaultValueTo(String type) {
		switch (type) {
			case "STRING":
				return "''";
			case "FLOAT":
				return "0.0";
			case "INTEGER":
				return "0";
			case "COLOR": 
				return "new Color()";
			case "VECTOR2":
				return "new Vector2()";
			case "GAME_OBJECT":
				return "null";
		}

		return null;
	}
	
	private String transpile_defaultExportValues(String js64, HashMap<String, List<String>> exportedProps) {
		for (Map.Entry<String, List<String>> entry : exportedProps.entrySet()) {
			String className = entry.getKey();
			List<String> propList = entry.getValue();
			Pattern compile = Pattern.compile("%"+className+"%");
			Matcher matcher = compile.matcher(js64);
			
			boolean find = matcher.find();
			if (find) {
				int start = matcher.start();
				int end = matcher.end();
				
				String defaults = "";
				
				for (int i = 0; i < propList.size(); i += 2) {
					String propName = propList.get(i);
					String propType = propList.get(i + 1);
					
					defaults += "this."+propName+" = "+defaultValueTo(propType)+"; ";
				}
				
				js64 = js64.substring(0, start) + defaults + js64.substring(end, js64.length());
			}
		}
		
		Pattern compile = Pattern.compile("%[A-z0-9_\\$]+%");
		Matcher matcher;
		while ((matcher = compile.matcher(js64)).find()) {
			int start = matcher.start();
			int end = matcher.end();

			js64 = js64.substring(0, start) + js64.substring(end, js64.length());
		}
		
		return js64;
	}

	private String transpile_methodSintax(String js64) {
		Pattern compile = Pattern.compile("[A-z0-9_\\$]+\\s*::\\s*[A-z0-9_\\$]+\\(");
		Matcher matcher;
		while ((matcher = compile.matcher(js64)).find()) {
			int start = matcher.start();
			int end = matcher.end();

			String className = js64.substring(start, js64.indexOf("::", start)).trim();
			String methodName = js64.substring(js64.indexOf("::", start) + 2, js64.indexOf("(", start)).trim();

			js64 = js64.substring(0, start) + className + ".prototype." + methodName + " = function (" + js64.substring(end, js64.length());
		}

		return js64;
	}


	private String transpile_referenceSintax(String js64) {
		Pattern compile = Pattern.compile("\\$[A-z0-9_\\$]+");
		Matcher matcher;
		while ((matcher = compile.matcher(js64)).find()) {
			int start = matcher.start();
			int end = matcher.end();

			String propName = js64.substring(start + 1, end).trim();

			js64 = js64.substring(0, start) + "this." + propName + js64.substring(end, js64.length());
		}

		return js64;
	}

	private String transpile_sharpCommentsSintax(String js64) {
		Pattern compile = Pattern.compile("\\s*#[^\r\n]+\n*");
		Matcher matcher;
		while ((matcher = compile.matcher(js64)).find()) {
			int start = matcher.start();
			int end = matcher.end();

			js64 = js64.substring(0, start) + "\n// " + js64.substring(js64.indexOf("#", start) + 1, end).trim() + "\n" + js64.substring(end, js64.length());
		}

		return js64;
	}
	
	public static class CompiledScript {
		public String name;
		public Scriptable objectScope;
		public HashMap<String, String> exportedProps = new HashMap<>();

		public CompiledScript(Scriptable objectScope) {
			this.objectScope = objectScope;
			
			name = (String) ScriptableObject.getProperty(objectScope, "name");
			
			Scriptable exports = (Scriptable) ScriptableObject.getProperty(objectScope, "exports");
			
			Object[] propertyIds = ScriptableObject.getPropertyIds(exports);
			for (int i = 0; i < propertyIds.length; i++) {
				String prop = (String) propertyIds[i];
				String propType = (String) ScriptableObject.getProperty(exports, prop);
				exportedProps.put(prop, propType);
				
				if (!ScriptableObject.hasProperty(objectScope, prop)) {
					ScriptableObject.putProperty(objectScope, prop, null);
				}
			}
		} 
	}
}
