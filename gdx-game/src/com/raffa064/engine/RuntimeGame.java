package com.raffa064.engine;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.raffa064.engine.core.App;
import com.raffa064.engine.core.SquareLib;
import com.raffa064.engine.core.ScriptEngine.*;
import org.mozilla.javascript.EvaluatorException;

public abstract class RuntimeGame extends Game {
	private int decodeKey;
	private EditorInterface android;
	private App app;
	private SquareLib lib = new SquareLib();

	public RuntimeGame(int decodeKey) {
		this.decodeKey = decodeKey;
	}
	
	public String trimLineSource(String lineSource, int offset) {
		return lineSource.substring(Math.max(0, offset - 10), Math.min(lineSource.length(), offset + 10));
	}
	
	public abstract void error(String message);
	
	@Override
	public void create() {
		try {
			app = new App(decodeKey);
			app.loadProject("project", false);
			app.scriptEngine.setErrorListener(new ErrorListener() {
				@Override
				public void warning(String message, String source, int lineNumber, String lineSource, int lineOffset) {
					String format = String.format("Script Error (%s:%d): %s\n%s", source, lineNumber, message, trimLineSource(lineSource, lineOffset));
					RuntimeGame.this.error(format);
				}

				@Override
				public void error(String message, String source, int lineNumber, String lineSource, int lineOffset) {
					String format = String.format("Script Error (%s:%d): %s\n%s", source, lineNumber, message, trimLineSource(lineSource, lineOffset));
					RuntimeGame.this.error(format);
				}

				@Override
				public EvaluatorException runtimeError(String message, String source, int lineNumber, String lineSource, int lineOffset) {
					String format = String.format("Script Error (%s:%d): %s\n%s", source, lineNumber, message, trimLineSource(lineSource, lineOffset));
					RuntimeGame.this.error(format);
					
					return new EvaluatorException(format);
				}
			});
		} catch(Exception e) {
			error("Load Error: "+e.getMessage());
		}
	}

	@Override
	public void render() {
		try {
			app.render(Gdx.graphics.getDeltaTime());
		} catch(Exception e) {
			
		}
	}

	@Override
	public void resize(int width, int height) {
		try {
			app.resize(width, height);
		} catch(Exception e) {

		}
	}

	@Override
	public void dispose() {
		try {
			app.dispose();
		} catch(Exception e) {

		}
	}
}
