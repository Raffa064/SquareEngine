package com.raffa064.engine.environments;

import com.badlogic.gdx.Game;
import com.raffa064.engine.core.App;
import com.raffa064.engine.core.ProjectConfigs;
import com.raffa064.engine.core.ScriptEngine.ErrorListener;
import com.raffa064.engine.core.SquareLib;
import com.raffa064.engine.environments.runtime.RuntimeGame;
import org.mozilla.javascript.EvaluatorException;
import com.raffa064.engine.core.OutputHandler;

/*
	Base class for runtime/editor environments
*/

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
	
	public String trimLineSource(String lineSource, int offset) {
		if (lineSource == null) {
			return "";
		}
		
		int size = 20;
		String trimmed = lineSource.substring(Math.max(0, offset - size), Math.min(lineSource.length(), offset + size));
		
		trimmed = trimmed.replace("\n", " ");
		
		return trimmed;
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
		String format = String.format("Script Warning\nWarning at %s:%d\n\"%s\"\n> %s", source, lineNumber, trimLineSource(lineSource, lineOffset), message);
		android.warning(format);
	}

	@Override
	public void error(String message, String source, int lineNumber, String lineSource, int lineOffset) {
		String format = String.format("Script Error\nError at %s:%d\n\"%s\"\n> %s", source, lineNumber, trimLineSource(lineSource, lineOffset), message);
		android.error(format, null);
	}

	@Override
	public EvaluatorException runtimeError(String message, String source, int lineNumber, String lineSource, int lineOffset) {
		String format = String.format("Script Runtime Error\nRuntime Error at %s:%d\n\"%s\"\n> %s", source, lineNumber, trimLineSource(lineSource, lineOffset), message);
		android.error(format, null);

		return null;
	}
}
