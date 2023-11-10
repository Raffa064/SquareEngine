package com.raffa064.engine.core;

import com.raffa064.engine.core.api.LoggerAPI;

public class Main {
	public static void main(String[] args) {
		ScriptEngine engine = new ScriptEngine();
		
		LoggerAPI logger = new LoggerAPI(null);
		
		engine.inject("logger", logger);
		
		engine.eval("function test() {};\n test.prototype.abc = function() {logger.log('abc')}\n logger.log(new test().abc)");
	}
}
