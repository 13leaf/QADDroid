package com.qad.render;

import java.util.List;

import com.qad.loader.ImageLoader;
import com.qad.loader.ImageLoader.ImageDisplayer;

import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;

public abstract class RenderEngine {
	
	/**
	 * 渲染一个View。将根据data的Render注解来查找并渲染相应控件。
	 * @param view
	 * @param data
	 * @param namespace
	 * @param loader
	 * @param displayer
	 */
	public static void render(View view,Object data,String namespace,ImageLoader loader,ImageDisplayer displayer){
		
	}
	
	public static void render(View view,Object data){
		render(view, data,"",null,null);
	}
	
	public static void render(View view,Object data,ImageLoader loader,ImageDisplayer displayer){
		render(view, data,"",loader,displayer);
	}
	
	/**
	 * 创建一个包含基本功能的渲染adapter
	 * @param layout 渲染所用的资源layout
	 * @param listData 渲染的数据源
	 * @param namespace 命名空间，详见{@link RenderType}
	 * @param loader 图片加载器
	 * @param displayer 图片显示策略
	 * @return
	 */
	public static RenderAdapter makeRenderAdapter(int layout,List<?> listData,String namespace,ImageLoader loader,ImageDisplayer displayer)
	{
		return new RenderAdapter(listData, layout, namespace, loader,displayer);
	}
	
	public static RenderAdapter makeRenderAdapter(int layout,List<?> listData)
	{
		return makeRenderAdapter(layout, listData,"",null,null);
	}
	
	public static RenderAdapter makeRenderAdapter(int layout,List<?> listData,ImageLoader loader,ImageDisplayer displayer)
	{
		return makeRenderAdapter(layout, listData,"",loader,null);
	}
	
	/**
	 * 渲染一个AdapterView。@see {@link RenderEngine#makeRenderAdapter(int, List, ImageLoader, ImageDisplayer)}
	 * @param adapterView 需渲染的目标AdapterView
	 * @param layout
	 * @param listData
	 * @param namespace
	 * @param loader
	 * @param displayer
	 */
	public static void render(AdapterView<Adapter> adapterView,int layout,List<?> listData,String namespace,ImageLoader loader,ImageDisplayer displayer)
	{
		adapterView.setAdapter(makeRenderAdapter(layout, listData,namespace,loader,displayer));
	}
	
	public static void render(AdapterView<Adapter> adapterView,int layout,List<?> listData)
	{
		render(adapterView, layout, listData,"",null,null);
	}
	
	public static void render(AdapterView<Adapter> adapterView,int layout,List<?> listData,ImageLoader loader,ImageDisplayer displayer)
	{
		render(adapterView, layout, listData,"",loader,displayer);
	}
	
}
