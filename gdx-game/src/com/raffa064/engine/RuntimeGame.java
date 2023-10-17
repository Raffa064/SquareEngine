package com.raffa064.engine;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.raffa064.engine.core.App;
import com.raffa064.engine.core.ProjectConfigs;
import com.raffa064.engine.core.ScriptEngine.ErrorListener;
import com.raffa064.engine.core.SquareLib;
import org.mozilla.javascript.EvaluatorException;

public abstract class RuntimeGame extends Game {
	private ProjectConfigs configs;
	private EditorInterface android;
	private App app;
	private SquareLib lib = new SquareLib();

	public void setConfigs(ProjectConfigs configs) {
		this.configs = configs;
	}

	public ProjectConfigs getConfigs() {
		return configs;
	}
	
	public String trimLineSource(String lineSource, int offset) {
		return lineSource.substring(Math.max(0, offset - 10), Math.min(lineSource.length(), offset + 10));
	}
	
	public abstract void error(String message);
	
	@Override
	public void create() {
		try {
			app = new App();
			app.loadProject(configs);
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
