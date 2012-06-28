package com.qad.demo.render;

import android.os.Bundle;

import com.qad.app.BaseActivity;
import com.qad.demo.R.drawable;
import com.qad.demo.R.id;
import com.qad.demo.R.layout;
import com.qad.loader.ImageLoader;
import com.qad.loader.service.LoadServices;
import com.qad.render.RenderEngine;

public class Render1 extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(layout.render1);
		RenderEntity1 entity1=new RenderEntity1();
		entity1.setAccount("once");
		entity1.setApprove(true);
		entity1.setEmail("xxx@gamil.com");
		entity1.setPassword("123123");
		entity1.setValidateAnswear("not really...");
		entity1.setValidateNumber("88ab");
		entity1.setValidatePicture("http://passport.csdn.net/ajax/verifyhandler.ashx?r=0.35602177726104856");
		
//		RenderEngine.render(findViewById(id.root), entity1);
		//work with loader
		ImageLoader loader=new ImageLoader(LoadServices.newHttpImageNoCache(me), getResources().getDrawable(drawable.icon));
		RenderEngine.render(findViewById(id.root), entity1,loader,new ImageLoader.DisplayShow());
	}
}
