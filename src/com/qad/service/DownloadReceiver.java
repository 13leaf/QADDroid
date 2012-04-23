package com.qad.service;

import com.qad.app.BaseBroadcastReceiver;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * 下载任务的接受器。继承本类并监听onNewDownload,onPublishProgress,onDownloadDone即可方便的处理。
 * 
 * @author 13leaf
 * 
 */
public class DownloadReceiver extends BaseBroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		//
		if (DownloadService.ACTION_PUBLISH_PROGRESS.equals(action)) {
			onPublishProgress(intent.getIntExtra(
					DownloadService.EXTRA_DOWNLOAD_PROGRESS, -1), context);
		} else if (DownloadService.ACTION_NEW_DOWNLOAD.equals(action)) {
			onNewDownload(
					intent.getStringExtra(DownloadService.EXTRA_DOWNLOAD_URL),
					intent.getStringExtra(DownloadService.EXTRA_TARGET_PATH),
					context);
		} else if (DownloadService.ACTION_DOWNLOAD_DONE.equals(action)) {
			onDownloadDone(intent.getBooleanExtra(
					DownloadService.EXTRA_DOWNLOAD_RESULT, false), context);
		}
	}

	/**
	 * 下载任务被接受，开始新的下载
	 * 
	 * @param downloadUrl
	 * @param targetPath
	 */
	protected void onNewDownload(String downloadUrl, String targetPath,
			Context context) {

	}

	/**
	 * 下载任务进度更新通知
	 * 
	 * @param progress
	 */
	protected void onPublishProgress(int progress, Context context) {

	}

	/**
	 * 下载任务完成
	 * 
	 * @param success
	 */
	protected void onDownloadDone(boolean success, Context context) {

	}

	@Override
	public IntentFilter getIntentFilter() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(DownloadService.ACTION_NEW_DOWNLOAD);
		filter.addAction(DownloadService.ACTION_PUBLISH_PROGRESS);
		filter.addAction(DownloadService.ACTION_DOWNLOAD_DONE);
		return filter;
	}

}
