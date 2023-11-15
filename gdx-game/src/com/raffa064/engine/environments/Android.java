package com.raffa064.engine.environments;
import com.raffa064.engine.core.OutputHandler;

/*
	Comunicate game to android features (editor/runtime)
*/

public interface Android extends OutputHandler {
    public void setOrientation(String orientation);
}
