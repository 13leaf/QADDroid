package com.qad.demo.page;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.qad.app.BaseActivity;
import com.qad.demo.R;
import com.qad.form.SimpleAsyncLoader;
import com.qad.form.SimpleLoadContent;
import com.qad.view.PageListView;
import com.qad.view.PageListView.PageAdapter;

public class PageListDemo extends BaseActivity{
	
	protected PageListView listView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		MockPageListLoader listLoader=new MockPageListLoader();
		listLoader.setProgressDialog(getDefaultProgressDialog());//开启progressDialog显示
//		listLoader.setProgressDialog(null);
		
//		PageListView listView=new PageListView(this, listLoader.getPager(),PageListView.MANUAL_MODE);//set show flag
//		listView.loadButton="加载新闻";listView.loadErrorMsg="新闻加载失败";listView.loadingMsg="加载新闻中...";
//		listView.setAdapter(new MockListAdapter(this, android.R.layout.simple_list_item_1));
//		setContentView(listView);
		
		//create by xml
		setContentView(R.layout.pagelist_demo);
		listView=(PageListView) findViewById(R.id.pageListView);
		listView.bindPageManager(listLoader.getPager());
		listView.setAdapter(new MockListAdapter(this, android.R.layout.simple_list_item_1));
	}
}


class MockListAdapter extends ArrayAdapter<String> implements PageAdapter<SimpleLoadContent<ArrayList<String>>>
{

	public MockListAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
	}

	@Override
	public void addPage(SimpleLoadContent<ArrayList<String>> pageContent) {
		for (String string : pageContent.getContent()) {
			add(string);
		}
	}
}

class MockPageListLoader extends SimpleAsyncLoader<ArrayList<String>>
{
	
	@Override
	public SimpleLoadContent<ArrayList<String>> asyncLoadPage(int pageNo, int pageSize)
			throws Exception {
		pageSize=8;
		pageNo--;
		SimpleLoadContent<ArrayList<String>> 
			content=new SimpleLoadContent<ArrayList<String>>();
		content.setPageNo(pageNo);
		content.setPageSum(5);
		
		ArrayList<String> arrayList=new ArrayList<String>();
		for(int i=pageNo*pageSize;i<pageNo*pageSize+pageSize;i++)
		{
			arrayList.add(i+"");
		}
		content.setContent(arrayList);
		
		Thread.sleep(500);//延迟
		
		return content;
	}
}
