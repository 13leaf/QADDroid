package com.qad.loader;

public interface LoaderCallback {
	void onLoading(boolean accept);
	void onLoadComplete();
	void onLoadFail();
	void onDestroy();
}
