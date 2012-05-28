package com.qad.view;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;

/**
 * 与ProgressDialog配合一起的异步任务。<br>
 * 使用它可以将异步任务与一个载入对话框绑定在一起.<br>
 * <ol>
 * <li>当异步任务开始，自动显示对话框。</li>
 * <li>当异步任务结束，自动消失对话框</li>
 * <li>当取消载入对话框，则自动cancel异步任务</li>
 * </ol>
 * <br>当然，我们可以设置是否将cancel对话框与cancel任务绑定在一起。默认情况是绑定的。
 * @deprecated 使用AsyncTask有几个bug:1.未处理onPostExecute时上下文切换的问题2.未处理横竖屏切换导致线程重启以及onPostExecute时上下文问题。使用Loader框架代替之
 * @author 13leaf
 * @param <Params>
 * @param <Progress>
 * @param <Result>
 *
 */
public abstract class ProgressAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

	private ProgressDialog mProgressDialog;
	
	/**
	 * 
	 * @param progressDialog
	 * @param cancelByDialog 当ProgressDialog被取消的时候，是否同时终止异步任务
	 */
	public ProgressAsyncTask(ProgressDialog progressDialog,boolean cancelByDialog)
	{
		super();
		mProgressDialog=progressDialog;
		if(cancelByDialog)
		{
			mProgressDialog.setCancelable(true);
			mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
				@Override
					public void onCancel(DialogInterface dialog) {
						cancel(true);
				}
			});
		}
	}
	
	public ProgressAsyncTask(ProgressDialog progressDialog)
	{
		this(progressDialog, true);
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		//make progress show
		mProgressDialog.show();
	}
	
	@Override
	protected void onPostExecute(Result result) 
	{
		//make progress hide
		mProgressDialog.dismiss();
	}
	
	
}
