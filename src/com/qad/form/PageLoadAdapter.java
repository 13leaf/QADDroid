package com.qad.form;

import com.qad.form.PageManager.PageLoadListener;

/**
	 * {@link PageLoadListener}的Adapter模式
	 * @author 13leaf
	 *
	 */
	public class PageLoadAdapter implements PageLoadListener{
		@Override
		public void onPageLoading(int loadPageNo, int pageSum) {};
		@Override
		public void onPageLoadComplete(int loadPageNo, int pageSum,Object content) {}
		@Override
		public void onPageLoadFail(int loadPageNo, int pageSum) {}
	}
	
