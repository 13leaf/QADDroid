package com.qad.demo.page;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import com.qad.app.BaseActivity;
import com.qad.demo.R;
import com.qad.demo.tool.DemoTools;
import com.qad.form.PageLoader;
import com.qad.form.PageManager;
import com.qad.form.PageManager.PageLoadListener;

public class PageDemo extends BaseActivity {
	
private MockPageLoader pageLoader;

private TextView txtLoad;

@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		wakeLock();
		
		setContentView(R.layout.page_bind_demo);
		
		pageLoader=new MockPageLoader(this);
		ProgressDialog mProgressDialog=new ProgressDialog(this);
		mProgressDialog.setMessage("载入中...请等待");
		
		PageManager<String> pager= pageLoader.getPager();//得到了绑定的PageManager，便无须关心翻页和加载的问题了。
		pager.bindPrevious(findViewById(R.id.btnPre));//将翻页操作与按钮进行简单的绑定
		pager.bindNext(findViewById(R.id.btnNext));
		
		txtLoad=(TextView) findViewById(R.id.loadTxt);
		
		txtLoad.setText("Not initialize");
		
		//设置监听器来处理各个加载状态
		pager.addOnPageLoadListioner(new PageLoadListener() {
			
			@Override
			public void onPageLoading(int loadPageNo, int pageSum) {
				txtLoad.setText("loading "+loadPageNo+",pageSum:"+pageSum);
			}
			
			@Override
			public void onPageLoadFail(int loadPageNo, int pageSum) {
				txtLoad.setText("fail load:"+loadPageNo+",pageSum="+pageSum);
			}
			
			@Override
			public void onPageLoadComplete(int loadPageNo, int pageSum, Object content) {
				txtLoad.setText("complete "+loadPageNo+",pageSum"+pageSum+",content="+content);
			}
		});
	}
}

/**
 * 实现一个PageLoader。注意如果loadPage方法不是同步的。因此当异步加载时，可以将加载的页号等信息传递给线程加以处理。<br>
 * 此处搭配使用了ProgressAsyncDialog,详细可以看 {@link ProgressAsyncTask}<br>
 * 实际使用时，设置的泛型参数应该是List或者其他自定义类型。<br>
 * @author 13leaf
 *
 */
class MockPageLoader implements PageLoader<String>{ 

	private PageManager<String> mPageManager;
	
	private ProgressDialog mProgressDialog;
	
	public MockPageLoader(Context context)
	{
		mPageManager=new PageManager<String>(this, 20);
		
		mProgressDialog=new ProgressDialog(context);
		mProgressDialog.setMessage("载入中..请稍后");
	}
	
	@Override
	public boolean loadPage(int pageNo, int pageSize) {
		new MyLoadTask(mProgressDialog,pageNo,pageSize).execute();
		return false;
	}

	@Override
	public PageManager<String> getPager() {
		return mPageManager;
	}
	
	class MyLoadTask extends AsyncTask<Void,Void,Void>
	{
		private int loadNo;
		
		private int loadPageSize;
		
		ProgressDialog dialog;

		public MyLoadTask(ProgressDialog progressDialog,int loadNo,int loadPageSize) {
			this.loadNo=loadNo;
			this.loadPageSize=loadPageSize;
			this.dialog=progressDialog;
			dialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				Thread.sleep(400);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			dialog.dismiss();
			//此处来通知PageManager更新页状态信息。
			mPageManager.notifyPageLoad((DemoTools.randomBoolean(5)?LOAD_COMPLETE:LOAD_FAIL)//以4:1的成功率来模拟加载
					,loadNo , 20, getLoadContent());
		}
		
		private String getLoadContent()
		{
			return "loadNo:"+loadNo+",loadPageSize:"+loadPageSize+",loadState:"
			+mPageManager.getLoadState()+",loadContent:"+
			(int)(Math.random()*1000);
		}
	}
}
