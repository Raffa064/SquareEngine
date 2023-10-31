package com.raffa064.engine.exporter;

public interface ExportListener {
	public void onSucess();
	public void onError(Throwable error);
	public void onFinished();
}
