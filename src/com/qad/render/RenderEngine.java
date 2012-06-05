package com.qad.render;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.EnumMap;
import java.util.List;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;

import com.qad.lang.Mirror;
import com.qad.loader.ImageLoader;
import com.qad.loader.ImageLoader.ImageDisplayer;
import com.qad.loader.LoadContext;
import com.qad.util.ViewTool;

public abstract class RenderEngine {

	static EnumMap<RenderType, String> mapping = new EnumMap<RenderType, String>(
			RenderType.class);

	static {
		mapping.put(RenderType.check, "setChecked");//boolean
		mapping.put(RenderType.hint, "setHint");//String
		mapping.put(RenderType.image, "setImageBitmap");//String
		mapping.put(RenderType.numStar, "setNumStars");//int
		mapping.put(RenderType.progress, "setProgress");//int
		mapping.put(RenderType.secondaryProgress, "setSecondaryProgress");//int
		mapping.put(RenderType.text, "setText");//Charsequence
		mapping.put(RenderType.textColor, "setTextColor");//int
		mapping.put(RenderType.visibility, "setVisibility");//int
	}

	/**
	 * 渲染一个View。将根据data的Render注解来查找并渲染相应控件。
	 * 
	 * @param view
	 * @param data
	 * @param loader
	 * @param displayer
	 */
	public static void render(View view, Object data, ImageLoader loader,
			ImageDisplayer displayer) {
		if (view == null || data == null) {
			throw new NullPointerException(String.format("view %s data %s",
					view, data));
		}
		Mirror<?> mirror=Mirror.me(data);
		ViewTool tool = new ViewTool(view);
		Field[] candidates=mirror.getType().isAnnotationPresent(RenderAll.class)?mirror.getFields():
				mirror.findFileds(Render.class);
		for (Field field : candidates) {
			// firstly find the target view
			Render mRender = field.getAnnotation(Render.class);
			View mView = null;
			int id=mRender!=null?
						mRender.id():0;
			String setter=null;
			RenderType type=mRender!=null?
								mRender.type():RenderType.auto;
			if(type==RenderType.none)
			{
				//do not want to render
				continue;
			}
			if (id== 0) {
				mView = tool.findViewByIdName(field.getName());
			} else {
				mView = view.findViewById(id);
			}
			if(mView==null){
				Log.e("RenderEngine","counl'd not find id "+field.getName());
				continue;
			}
			//secondly find view render method
			if (type == RenderType.auto) {
				// detect by field type
				Mirror<?> m = Mirror.me(field.getType());
				if (m.isStringLike()) {
					setter=mapping.get(RenderType.text);
				}else if(m.isBoolean()){
					setter=mapping.get(RenderType.check);
				}
			}else if(type==RenderType.custom){
				setter=mRender.setter();
			}else {
				setter=mapping.get(type);
			}
			
			//specially for download image
			if(type==RenderType.image)
			{
				if(loader!=null)
					loader.startLoading(
							new LoadContext<String, ImageView, Bitmap>(mirror.getValue(data, field)+"", (ImageView)mView),displayer);
				continue;
			}
			//thirdly invoke render method
			try {
				Method setMethod=Mirror.me(mView).findMethod(setter, field.getType());
				//do render
				setMethod.invoke(mView, mirror.getValue(data, field.getName()));
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
		//render by method
		Method[] candidates2=mirror.getMethods(Render.class);
		for(Method method:candidates2)
		{
			if(method.getReturnType()==Void.class)
				throw new RuntimeException("Renderable method return type can not be null!");
			if(method.getParameterTypes().length!=0)
				throw new RuntimeException("Renderable method paramater must be empty!");
			method.setAccessible(true);//ensure invokeable
			Render mRender = method.getAnnotation(Render.class);
			View mView = null;
			int id=mRender!=null?
						mRender.id():0;
			String setter=null;
			RenderType type=mRender!=null?
								mRender.type():RenderType.auto;
			if(type==RenderType.none)
			{
				//do not want to render
				continue;
			}
			if (id== 0) {
				throw new IllegalArgumentException("use @Render for method,you must to set id param!");
			} else {
				mView = view.findViewById(id);
			}
			//secondly find view render method
			if (type == RenderType.auto) {
				// detect by field type
				Mirror<?> m = Mirror.me(method.getReturnType());
				if (m.isStringLike()) {
					setter=mapping.get(RenderType.text);
				}else if(m.isBoolean()){
					setter=mapping.get(RenderType.check);
				}
			}else if(type==RenderType.custom){
				setter=mRender.setter();
			}else {
				setter=mapping.get(type);
			}
			
			//specially for download image
			if(type==RenderType.image)
			{
				if(loader!=null)
					try {
						loader.startLoading(
								new LoadContext<String, ImageView, Bitmap>(method.invoke(data)+"", (ImageView)mView),displayer);
					} catch (Exception e){
						e.printStackTrace();
					}
				continue;
			}
			//thirdly invoke render method
			try {
				Method setMethod=Mirror.me(mView).findMethod(setter, method.getReturnType());
				//do render
				setMethod.invoke(mView, method.invoke(data));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void render(View view, Object data) {
		render(view, data, null, null);
	}

	/**
	 * 创建一个包含基本功能的渲染adapter
	 * 
	 * @param factory
	 *            渲染制造的adapter项工厂
	 * @param listData
	 *            渲染的数据源
	 * @param namespace
	 *            命名空间，详见{@link RenderType}
	 * @param loader
	 *            图片加载器
	 * @param displayer
	 *            图片显示策略
	 * @return
	 */
	public static RenderAdapter makeRenderAdapter(ViewFactory factory, List<?> listData,
			ImageLoader loader, ImageDisplayer displayer) {
		return new RenderAdapter(listData, factory, loader, displayer);
	}
	
	public static RenderAdapter makeRenderAdapter(int layout, List<?> listData,
			ImageLoader loader, ImageDisplayer displayer){
		return new RenderAdapter(listData, layout, loader, displayer);
	}

	public static RenderAdapter makeRenderAdapter(int layout, List<?> listData) {
		return makeRenderAdapter(layout, listData, null, null);
	}
	
	public static RenderAdapter makeRenderAdapter(ViewFactory factory, List<?> listData) {
		return makeRenderAdapter(factory, listData,null,null);
	}
	

	/**
	 * 渲染一个AdapterView。@see
	 * {@link RenderEngine#makeRenderAdapter(int, List, ImageLoader, ImageDisplayer)}
	 * 
	 * @param adapterView
	 *            需渲染的目标AdapterView
	 * @param factory
	 * @param listData
	 * @param namespace
	 * @param loader
	 * @param displayer
	 */
	public static void render(AbsListView adapterView, ViewFactory factory,
			List<?> listData, ImageLoader loader, ImageDisplayer displayer) {
		adapterView.setAdapter(makeRenderAdapter(factory, listData, loader,
				displayer));
	}

	public static void render(AbsListView adapterView, ViewFactory factory,
			List<?> listData) {
		render(adapterView, factory, listData, null, null);
	}
	
	public static void render(AbsListView adapterView, int layout,
			List<?> listData, ImageLoader loader, ImageDisplayer displayer) {
		adapterView.setAdapter(makeRenderAdapter(layout, listData, loader,
				displayer));
	}

	public static void render(AbsListView adapterView, int layout,
			List<?> listData){
		render(adapterView,layout,listData,null,null);
	}
}
