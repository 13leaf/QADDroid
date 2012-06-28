package com.qad.demo.page;

import com.qad.form.PageLoadAdapter;
import com.qad.form.PageManager;
import com.qad.view.PageListView;

import android.os.Bundle;


public class CustomListTrigger extends PageListDemo {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PageManager<?> mPageManager= listView.getPageManager();
		final PageListView mListView=listView;
		mPageManager.addOnPageLoadListioner(new PageLoadAdapter(){
			public void onPageLoadComplete(int loadPageNo, int pageSum, Object content) {
				if(loadPageNo==2){
					mListView.setTriggerMode(PageListView.MANUAL_MODE);
				}
			};
		});
	}
}
