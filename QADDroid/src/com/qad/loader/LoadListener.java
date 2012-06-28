package com.qad.loader;

public interface LoadListener {
	
	void loadComplete(LoadContext<?, ?, ?> context);
	
	void loadFail(LoadContext<?, ?, ?> context);
}
