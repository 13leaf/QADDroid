package com.qad.service;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.content.Context;
import android.content.Intent;

import com.qad.annotation.InjectExtras;
import com.qad.net.Downloader;
import com.qad.net.Downloader.PublishCallBack;

/**
 * 提供一个单线程工作的下载服务。每次接受一个下载任务然后进行处理。接受参数必须有downloadUrl和targetPath两项。<br>
 * 特别注意的是:preDownload,doDownload,postDownload都在非UI的单线程中。<br>
 * 
 * @author 13leaf
 * 
 */
public class DownloadService extends BaseIntentService implements
		PublishCallBack {

	// recevice listener for that event.
	public static final String ACTION_ENQUEUE = "action.com.qad.service.enqueue";
	public static final String ACTION_NEW_DOWNLOAD = "action.com.qad.service.download";
	public static final String ACTION_PUBLISH_PROGRESS = "action.com.qad.service.publish_progress";
	public static final String ACTION_DOWNLOAD_DONE = "action.com.qad.service.download_done";

	// in
	public static final String EXTRA_DOWNLOAD_URL = "extra.com.qad.service.download_url";
	public static final String EXTRA_TARGET_PATH = "extra.com.qad.service.target_path";
	// out
	public static final String EXTRA_DOWNLOAD_PROGRESS = "extra.com.qad.service.download_progress";
	public static final String EXTRA_DOWNLOAD_RESULT = "extra.com.qad.service.download_result";

	private ConcurrentLinkedQueue<String> tasks = new ConcurrentLinkedQueue<String>();
	private static final String SPLIT = "_";

	public static void start(Context context, String downloadUrl,
			String downloadTarget, Intent extras) {
		Intent intent = new Intent(context, DownloadService.class);
		intent.putExtra(EXTRA_DOWNLOAD_URL, downloadUrl);
		intent.putExtra(EXTRA_TARGET_PATH, downloadTarget);
		if (extras != null)
			intent.putExtras(extras);
		context.startService(intent);
	}

	public DownloadService() {
		super("DownloadService");
	}

	// don't use for other thread.It only used for HandleThread
	@InjectExtras(name = EXTRA_DOWNLOAD_URL)
	protected String currentDownloadUrl;
	@InjectExtras(name = EXTRA_TARGET_PATH)
	protected String currentDownTarget;

	protected Intent currentIntent;
	private int currentProgress;

	@Override
	public void onStart(Intent intent, int startId) {
		if(enqueueDownload(intent)){
			super.onStart(intent, startId);
		}
	}

	/**
	 * 
	 * @param intent
	 * @return 若接受请求,则返回true,否则false
	 */
	protected boolean enqueueDownload(Intent intent) {
		if (intent == null)
			return false;
		String downUrl = intent.getStringExtra(EXTRA_DOWNLOAD_URL);
		String downPath = intent.getStringExtra(EXTRA_TARGET_PATH);
		if (downUrl != null && downPath != null
				&& !tasks.contains(downUrl + SPLIT + downPath)){
			tasks.add(downUrl+SPLIT+downPath);
			sendEnqueue(intent);
			return true;
		}
		else{
			debugLog("Prevent invalid or duplicate task:"+downUrl+SPLIT+downPath);
			return false;
		}
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		super.onHandleIntent(intent);
		currentIntent = intent;
		try {
			currentProgress = -1;// reset
			preDownload();
			sendNewDownload();
			boolean success = doDownload();
			postDownload();
			sendDone(success);
		} catch (Exception e) {
			e.printStackTrace();
			sendDone(false);
		}
	}

	/**
	 * 下载前准备工作。子类覆盖此处来做一些下载准备工作
	 */
	protected void preDownload() {
		debugLog(String.format("preDownload %s %s", currentDownloadUrl,
				currentDownTarget));
	}

	/**
	 * 做下载工作
	 * 
	 * @throws IOException
	 * @return 返回是否下载时被中断
	 */
	protected boolean doDownload() throws IOException {
		debugLog(String.format("startDownload %s %s", currentDownloadUrl,
				currentDownTarget));
		return Downloader.downLoad(currentDownloadUrl, currentDownTarget,
				Downloader.DEFAULT_BLOCK_SIZE, false, this);
	}

	/**
	 * 完成下载的后续准备工作。
	 * 
	 * @throws 抛出异常将会通知下载失败
	 */
	protected void postDownload() throws Exception {

	}

	@Override
	public boolean publish(long downSize, long fullSize, int percent) {
		if (percent != currentProgress) {
			currentProgress = percent;
			debugLog(String.format("publish progress %s %s %s-%s", downSize,
					fullSize, percent, currentDownloadUrl));
			sendPublish(percent);
		}
		return false;
	}

	private void sendEnqueue(Intent origin) {
		Intent intent = new Intent(ACTION_ENQUEUE);
		intent.putExtras(origin);
		sendBroadcast(intent);
	}

	private void sendNewDownload() {
		Intent broadCastIntent = new Intent(ACTION_NEW_DOWNLOAD);
		broadCastIntent.putExtras(currentIntent);
		sendBroadcast(broadCastIntent);
	}

	private void sendPublish(int percent) {
		Intent intent = new Intent(ACTION_PUBLISH_PROGRESS);
		intent.putExtras(currentIntent);
		intent.putExtra(EXTRA_DOWNLOAD_PROGRESS, percent);
		sendBroadcast(intent);
	}

	private void sendDone(boolean success) {
		Intent intent = new Intent(ACTION_DOWNLOAD_DONE);
		intent.putExtras(currentIntent);
		intent.putExtra(EXTRA_DOWNLOAD_RESULT, success);
		sendBroadcast(intent);
		tasks.remove(intent.getStringExtra(EXTRA_DOWNLOAD_URL) + SPLIT
				+ intent.getStringExtra(EXTRA_TARGET_PATH));
		debugLog(String.format("send Done %s %s", currentDownloadUrl, success));
	}
}
