package com.qad.demo.render;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.qad.app.BaseListActivity;
import com.qad.demo.R.layout;
import com.qad.render.RenderEngine;
import com.qad.render.ViewFactory;

public class Render3 extends BaseListActivity implements ViewFactory{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		RenderEngine.render(getListView(), this, buildEntities());
	}
	
	public ArrayList<RenderEntity3> buildEntities()
	{
		ArrayList<RenderEntity3> a=new ArrayList<RenderEntity3>();
		for(int i=0;i<50;i++)
		{
			a.add(new RenderEntity3("A quick brown fox jumps over the lazy dog"+i));
		}
		return a;
	}

	@Override
	public View createView(Context context, int position) {
		return getLayoutInflater().inflate(layout.render3, null);
	}

	@Override
	public void render(View view,Object data,int position) {
		if(position%2==0){
			view.setBackgroundColor(Color.DKGRAY);
		}else {
			view.setBackgroundColor(Color.BLACK);
		}
	}
}
