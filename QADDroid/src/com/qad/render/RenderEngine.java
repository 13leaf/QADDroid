package com.qad.render;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;

import com.qad.lang.Mirror;
import com.qad.loader.ImageLoader;
import com.qad.loader.ImageLoader.ImageDisplayer;
import com.qad.loader.LoadContext;
import com.qad.util.ViewTool;

public abstract class RenderEngine {

	static EnumMap<RenderType, String> mapping = new EnumMap<RenderType, String>(
			RenderType.class);

	static {
		mapping.put(RenderType.check, "setChecked");// boolean
		mapping.put(RenderType.hint, "setHint");// String
		mapping.put(RenderType.numStar, "setNumStars");// int
		mapping.put(RenderType.progress, "setProgress");// int
		mapping.put(RenderType.secondaryProgress, "setSecondaryProgress");// int
		mapping.put(RenderType.text, "setText");// Charsequence
		mapping.put(RenderType.textColor, "setTextColor");// int
		mapping.put(RenderType.visibility, "setVisibility");// int
	}

	private static boolean cache = true;

	/**
	 * 开启Cache将会缓存view与object的映射关系。尤其会对adapterView有很显著的优化作用<br>
	 * <b>注意:cache的映射关系会缓存在view的tag中。</b>如果tag另有它途，请使用setTag(id,object)。<br>
	 * 关闭cache不会将映射关系存入tag。
	 * 
	 * @param cache
	 */
	public static void setCache(boolean cache) {
		RenderEngine.cache = cache;
	}

	private static class RenderHierarchy {
		final Field[] provideFields;
		final Method[] provideMethods;

		// 缓存目标渲染view对应的渲染方法
		RenderHierarchy(Field[] provideFields, Method[] provideMethods) {
			this.provideFields = provideFields;
			this.provideMethods = provideMethods;
		}

	}

	private static class AnalyzedData {
		View targetView;
		Method renderMethod;
		RenderType renderType;
		Object renderData;
		Field provideField;
		Method provideMethod;
	}

	// 缓存对应某个数据类型下的所有渲染内容
	private static HashMap<Class<?>, RenderHierarchy> cachedRenderHierarchy = new HashMap<Class<?>, RenderHierarchy>();

	/**
	 * 分析目标渲染View，若没有提供id。则尝试根据字段名反射获得id来查找
	 * 
	 * @param wrapper
	 * @param render
	 * @param field
	 * @return
	 */
	private static View analyzeTargetView(ViewTool wrapper, Render render,
			Field field) {
		View mView = null;
		int id = (render == null ? 0 : render.id());
		if (id == 0) {
			mView = wrapper.findViewByIdName(field.getName());
		} else {
			mView = wrapper.getView().findViewById(id);
		}
		if (mView == null) {
			Log.e("RenderEngine", "counl'd not find id " + field.getName());
		}
		return mView;
	}

	/**
	 * 分析setter名
	 * 
	 * @param providerDataType
	 *            提供者的数据类型
	 * @param render
	 *            Render注解,若为空,则默认RenderType为auto。
	 * @return
	 */
	private static String analyzeSetter(Class<?> providerDataType, Render render) {
		String setter = null;
		RenderType type = (render == null ? RenderType.auto : render.type());
		// analyze inject setter
		if (type == RenderType.none)
			return null;
		if (type == RenderType.auto) {
			// detect by field type
			Mirror<?> m = Mirror.me(providerDataType);
			if (m.isStringLike()) {
				setter = mapping.get(RenderType.text);
			} else if (m.isBoolean()) {
				setter = mapping.get(RenderType.check);
			}
		} else if (type == RenderType.custom) {
			setter = render.setter();
		} else {
			setter = mapping.get(type);
		}
		return setter;
	}

	private static Object fetchRenderData(Method provideMethod, Object rootData) {
		try {
			return provideMethod.invoke(rootData);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static Object fetchRenderData(Field provideField, Object rootData) {
		return Mirror.me(rootData).getValue(rootData, provideField);
	}

	private static AnalyzedData analyze(Object data, ViewTool wrapper,
			Method provideMethod) {
		AnalyzedData entry = new AnalyzedData();

		Render render = provideMethod.getAnnotation(Render.class);
		entry.targetView = wrapper.getView().findViewById(render.id());//
		if (entry.targetView == null) {
			Log.e("RenderEngine",
					"counl'd not find id by" + provideMethod.getName());
		}
		entry.renderType = (render == null ? RenderType.auto : render.type());

		String setter = analyzeSetter(provideMethod.getReturnType(), render);
		if (entry.targetView != null && setter != null) {
			try {
				entry.renderMethod = Mirror.me(entry.targetView).findMethod(
						setter, provideMethod.getReturnType());
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
		}
		return entry;
	}

	private static AnalyzedData analyze(Object data, ViewTool wrapper,
			Field provideField) {
		AnalyzedData entry = new AnalyzedData();

		Render render = provideField.getAnnotation(Render.class);
		entry.targetView = analyzeTargetView(wrapper, render, provideField);
		entry.renderType = render == null ? RenderType.auto : render.type();

		String setter = analyzeSetter(provideField.getType(), render);
		if (entry.targetView != null && setter != null) {
			try {
				entry.renderMethod = Mirror.me(entry.targetView).findMethod(
						setter, provideField.getType());
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
		}
		return entry;
	}

	/**
	 * 执行Render,若为image类型。则执行loader下载任务 否则尝试执行注入
	 * 
	 * @param entry
	 * @param loader
	 * @param displayer
	 */
	private static void executeRender(AnalyzedData entry, ImageLoader loader,
			ImageDisplayer displayer) {
		if (entry.renderData == null)
			return;
		if (entry.renderType == RenderType.image && loader != null) {
			loader.startLoading(new LoadContext<String, ImageView, Bitmap>(
					entry.renderData + "", (ImageView) entry.targetView),
					displayer);
		}
		if (entry.renderMethod == null)
			return;
		try {
			// do render
			entry.renderMethod.invoke(entry.targetView, entry.renderData);
		} catch (Exception e) {
			e.printStackTrace();
		}
		entry.renderData = null;// recyle data
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
		// TODO 增加cache，加速映射效率.重构render方法

		if (view == null || data == null) {
			throw new NullPointerException(String.format("view %s data %s",
					view, data));
		}
		Mirror<?> mirror = Mirror.me(data);
		ViewTool wrapper = new ViewTool(view);

		Field[] candidates = null;
		Method[] candidates2 = null;
		RenderHierarchy cachedHierarchy = cachedRenderHierarchy.get(data
				.getClass());

		if (cachedHierarchy == null) {
			candidates = mirror.getType().isAnnotationPresent(RenderAll.class) ? mirror
					.getFields() : mirror.findFileds(Render.class);
			candidates2 = mirror.getMethods(Render.class);
			cachedRenderHierarchy.put(data.getClass(), new RenderHierarchy(
					candidates, candidates2));
		} else {
			candidates = cachedHierarchy.provideFields;
			candidates2 = cachedHierarchy.provideMethods;
		}

		@SuppressWarnings("unchecked")
		HashMap<Class<?>, ArrayList<AnalyzedData>> cachedData = (HashMap<Class<?>, ArrayList<AnalyzedData>>) view
				.getTag();
		ArrayList<AnalyzedData> analyzedEntries = null;
		//try to load by cache
		if(cachedData!=null){
			analyzedEntries=cachedData.get(data.getClass());
		}
		if(analyzedEntries==null){
			analyzedEntries=new ArrayList<RenderEngine.AnalyzedData>();
			// analyze field
			for (Field field : candidates) {
				field.setAccessible(true);
				AnalyzedData analyzedData = analyze(data, wrapper, field);
				analyzedData.provideField = field;
				analyzedEntries.add(analyzedData);
			}
			// analyze method
			for (Method method : candidates2) {
				// check
				if (method.getReturnType() == Void.class)
					throw new RuntimeException(
							"Renderable method return type can not be null!");
				if (method.getParameterTypes().length != 0)
					throw new RuntimeException(
							"Renderable method paramater must be empty!");
				method.setAccessible(true);// ensure invokeable
				AnalyzedData analyzedData = analyze(data, wrapper, method);
				analyzedData.provideMethod = method;
				analyzedEntries.add(analyzedData);
			}
			//save cache
			if(cache){
				if(cachedData==null) cachedData=new HashMap<Class<?>, ArrayList<AnalyzedData>>();
				cachedData.put(data.getClass(), analyzedEntries);
				view.setTag(cachedData);
			}
		}

		// fill render data
		for (AnalyzedData renderEntry : analyzedEntries) {
			if (renderEntry.provideField != null)
				renderEntry.renderData = fetchRenderData(
						renderEntry.provideField, data);
			else if (renderEntry.provideMethod != null)
				renderEntry.renderData = fetchRenderData(
						renderEntry.provideMethod, data);
		}

		// execute render
		for (AnalyzedData renderEntry : analyzedEntries) {
			if (renderEntry.renderType == RenderType.object
					&& renderEntry.renderData != null) {
				// 递归渲染
				render(renderEntry.targetView == null ? view
						: renderEntry.targetView, renderEntry.renderData,
						loader, displayer);
			}
			executeRender(renderEntry, loader, displayer);
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
	public static RenderAdapter makeRenderAdapter(ViewFactory factory,
			List<?> listData, ImageLoader loader, ImageDisplayer displayer) {
		return new RenderAdapter(listData, factory, loader, displayer);
	}

	public static RenderAdapter makeRenderAdapter(int layout, List<?> listData,
			ImageLoader loader, ImageDisplayer displayer) {
		return new RenderAdapter(listData, layout, loader, displayer);
	}

	public static RenderAdapter makeRenderAdapter(int layout, List<?> listData) {
		return makeRenderAdapter(layout, listData, null, null);
	}

	public static RenderAdapter makeRenderAdapter(ViewFactory factory,
			List<?> listData) {
		return makeRenderAdapter(factory, listData, null, null);
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
		// XXX compat
		// issue:http://stackoverflow.com/questions/5824267/did-honeycomb-sdk-break-gridview-backward-compatibility
		((AdapterView<ListAdapter>) adapterView).setAdapter(makeRenderAdapter(
				factory, listData, loader, displayer));
	}

	public static void render(AbsListView adapterView, ViewFactory factory,
			List<?> listData) {
		render(adapterView, factory, listData, null, null);
	}

	public static void render(AbsListView adapterView, int layout,
			List<?> listData, ImageLoader loader, ImageDisplayer displayer) {
		((AdapterView<ListAdapter>) adapterView).setAdapter(makeRenderAdapter(
				layout, listData, loader, displayer));
	}

	public static void render(AbsListView adapterView, int layout,
			List<?> listData) {
		render(adapterView, layout, listData, null, null);
	}
}
