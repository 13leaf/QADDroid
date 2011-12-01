package com.qad.form;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.qad.view.ProgressAsyncTask;

/**
 * 实现一个简单的异步加载PageLoader。通常情况下，你只需要实现asyncLoadPage抽象方法即可。<br>
 * 若希望在载入过程中显示等待对话框，那么可以调用setProgressDialog()。
 * @author 13leaf
 * @deprecated 使用AsyncTask存在一些问题。
 */
public abstract class SimpleAsyncLoader<Content> implements PageLoader<SimpleLoadContent<Content>> {
	
	private PageManager<SimpleLoadContent<Content>> mPageManager;
	
	private ProgressDialog mProgressDialog;
	
	/**
	 * 如若设置了ProgressDialog,那么将在异步加载的时候显示一个进度对话框。
	 * @param progressDialog
	 * @param defaultLoadSize
	 */
	public SimpleAsyncLoader(ProgressDialog progressDialog,int defaultLoadSize)
	{
		mPageManager=new PageManager<SimpleLoadContent<Content>>(this, defaultLoadSize);
		mProgressDialog=progressDialog;
	}
	
	/**
	 * 使用每页加载20条记录的设置,默认关闭ProgressDialog
	 * 如若想在加载过程中加入ProgressDialog,请调用重载函数。
	 */
	public SimpleAsyncLoader()
	{
		this(null,20);
	}
	
	/**
	 * 请勿覆盖本方法，需要实现载入逻辑应当重写asyncLoadPage
	 */
	@Override
	public boolean loadPage(int pageNo, int pageSize) {
		if(mProgressDialog==null)
		{
			new MyAsyncLoadTask().execute(pageNo,pageSize);
		}else {
			new MyAsyncLoadTask2(mProgressDialog).execute(pageNo,pageSize);
		}
		return false;
	}
	
	/**
	 * 设置异步加载过程中显示的等待对话框，若设置了该属性将默认开启ProgressDialog显示。<br>
	 * 若在加载过程中取消了ProgressDialog，异步加载任务也会被同样取消。<br>
	 * 如想关闭加载对话框的显示，那么请setProgressDialog(null)即可
	 * @param progressDialog
	 */
	public void setProgressDialog(ProgressDialog progressDialog)
	{
		mProgressDialog=progressDialog;
	}
	
	public ProgressDialog getProgressDialog()
	{
		return mProgressDialog;
	}
	
	/**
	 * 实现如何异步加载页内容的方法。该方法会在新线程中被调用，因此无需在方法内部再使用线程。<br>
	 * 若在载入逻辑中返回一个null的结果。那么会被SimpeAsyncLoader处理为载入失败。否则认为载入成功。
	 * @param pageNo
	 * @param pageSize
	 * @throws quick dirty thing
	 * @return
	 */
	public abstract SimpleLoadContent<Content> asyncLoadPage(int pageNo,int pageSize) throws Exception;
	
	@Override
	public PageManager<SimpleLoadContent<Content>> getPager() {
		return mPageManager;
	}
	
	private final class MyAsyncLoadTask extends AsyncTask<Integer, Void,SimpleLoadContent<Content>>
	{
		int loadNo;
		int loadPageSize;
		
		@Override
		protected SimpleLoadContent<Content> doInBackground(Integer... params) {
			loadNo=params[0];
			loadPageSize=params[1];
			
			try {
				return asyncLoadPage(loadNo, loadPageSize);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		
		protected void onPostExecute(SimpleLoadContent<Content> result) {
			if(result==null)
			{
				mPageManager.notifyPageLoad(LOAD_FAIL,
						loadNo, mPageManager.getPageSum(), 
						null);
			}else {
				mPageManager.notifyPageLoad(LOAD_COMPLETE,
						loadNo, result.getPageSum(), 
						result);
			}
		}
		
		
	}
	
	private final class MyAsyncLoadTask2 extends ProgressAsyncTask<Integer,Void,SimpleLoadContent<Content>>
	{
		int loadNo;
		int loadPageSize;
		
		public MyAsyncLoadTask2(ProgressDialog progressDialog) {
			super(progressDialog);
		}
		
		@Override
		protected SimpleLoadContent<Content> doInBackground(Integer... params) {
			loadNo=params[0];
			loadPageSize=params[1];
			 
			try {
				return asyncLoadPage(loadNo, loadPageSize);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		
		protected void onPostExecute(SimpleLoadContent<Content> result) {
			super.onPostExecute(result);//通知等待对话框的消失
			
			if(result==null)
			{
				mPageManager.notifyPageLoad(LOAD_FAIL,
						loadNo, mPageManager.getPageSum(), 
						null);
			}else {
				mPageManager.notifyPageLoad(LOAD_COMPLETE,
						loadNo, result.getPageSum(), 
						result);
			}
		}
		
		@Override
		protected void onCancelled() {
			super.onCancelled();
			
			mPageManager.notifyPageLoad(LOAD_FAIL, loadNo, mPageManager.getPageSum(), null);
		}
	}
	

}
