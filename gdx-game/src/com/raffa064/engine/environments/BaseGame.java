package com.raffa064.engine.environments;

import com.badlogic.gdx.Game;
import com.raffa064.engine.core.App;
import com.raffa064.engine.core.ProjectConfigs;
import com.raffa064.engine.core.ScriptEngine.ErrorListener;
import com.raffa064.engine.core.SquareLib;
import com.raffa064.engine.environments.runtime.RuntimeGame;
import org.mozilla.javascript.EvaluatorException;

public abstract class BaseGame extends Game implements ErrorListener {
	protected Android android;
	protected ProjectConfigs configs;
	protected App app;
	protected SquareLib lib = new SquareLib();
	
	public BaseGame(Android android) {
		this.android = android;
	}
	
	public void setConfigs(ProjectConfigs configs) {
		this.configs = configs;
	}

	public ProjectConfigs getConfigs() {
		return configs;
	}

	public abstract void loadProject();
	
	public abstract void error(String message);
	
	public String trimLineSource(String lineSource, int offset) {
		return lineSource.substring(Math.max(0, offset - 10), Math.min(lineSource.length(), offset + 10));
	}

	@Override
	public void create() {
		loadProject();
	}

	@Override
	public void dispose() {
		super.dispose();
		
		app.dispose();
	}
	
	@Override
	public void warning(String message, String source, int lineNumber, String lineSource, int lineOffset) {
		String format = String.format("Script Error (%s:%d): %s\n%s", source, lineNumber, message, trimLineSource(lineSource, lineOffset));
		error(format);
	}

	@Override
	public void error(String message, String source, int lineNumber, String lineSource, int lineOffset) {
		String format = String.format("Script Error (%s:%d): %s\n%s", source, lineNumber, message, trimLineSource(lineSource, lineOffset));
		error(format);
	}

	@Override
	public EvaluatorException runtimeError(String message, String source, int lineNumber, String lineSource, int lineOffset) {
		String format = String.format("Script Error (%s:%d): %s\n%s", source, lineNumber, message, trimLineSource(lineSource, lineOffset));
		error(format);

		return new EvaluatorException(format);
	}
}
