package com.qad.demo.group;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.qad.demo.R;
import com.qad.demo.R.id;
import com.qad.demo.page.PageDemo;
import com.qad.form.MasterActivity;

//FIXME 若MasterActivity声明处理orientation转屏事件,则此时子Activity只能也自行处理orientation转屏事件
public class MasterDemo extends TestMasterActivity{
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.group_main);
		
		setCreateOnce(true);//设置每次导航都销毁重建
		
		//设置绑定控件的id号,绑定的本地活动目标,绑定的标题
		bindNavigate(R.id.navi_hot, ActivityBody.class,"ActivityBody");//设置其首要显示
		bindNavigate(R.id.navi_live, ActivityBody2.class,"ActivityBody2");
//		bindNavigate(id.navi_audio, DynamicDialog.class,"DynamicDialog");
//		navigate(id.navi_hot);
		
		//设置导航发生时的监听器
		setOnNavigateListener(new onNavigateListener() {
			@Override
			public boolean onNavigation(String tag, Intent intent) {
				showMessage("navigate -> "+tag);
				return false;
			}
		});
		//若希望新启动一个活动,则仍然非常简单的
		findViewById(R.id.navi_audio).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(PageDemo.class);
			}
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(com.qad.demo.R.menu.master, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case id.masterItem:
			showMessage("master handle it!");
			break;
		case id.masterItem2:
//			navigate(id.navi_live);
			navigate("ActivityBody2");

		}
		return super.onOptionsItemSelected(item);
	}
}
