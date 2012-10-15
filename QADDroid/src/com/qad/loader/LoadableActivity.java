package com.qad.loader;

import java.io.Serializable;

import android.os.Bundle;
import android.view.View;

import com.qad.app.BaseActivity;
import com.qad.render.RenderEngine;

/**
 * 子类必须实现startLoading方法。可调用Loader{@link AbstractLoader}去执行一个加载任务。<br>
 * 如想要让Loader与状态切换绑定。则只需要将包装的Stateable通过getStateAble()<br>
 * @author 13leaf
 *
 * @param <T>
 */
public abstract class LoadableActivity<T extends Serializable> extends BaseActivity implements LoadListener,onRetryListener{

	/**
	 * 获取泛型类型。泛型擦除导致的
	 * @return
	 */
	public abstract Class<T> getGenericType();
	
	/**
	 * 获得状态切换机，如果存在，将与loader绑定。<br>
	 * 若没有状态机，则直接返回null
	 * @return
	 */
	public abstract StateAble getStateAble();
	
	/**
	 * 获取加载器
	 * @return
	 */
	public abstract BeanLoader getLoader();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getLoader().addListener(this,getGenericType());
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		getLoader().removeListener(this,getGenericType());
	}
	
	/**
	 * 开始加载。与requestLoading不同，它包含了状态切换，因此务必尽量使用startLoading
	 */
	public void startLoading(){
		StateAble stateAble=getStateAble();
		if(stateAble!=null){
			stateAble.showLoading();
		}
	}
	
	/**
	 * 渲染视图，子类根据需求可以重写它
	 * @param data
	 */
	public void render(T data){
		RenderEngine.render(getWindow().getDecorView(), data);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void loadComplete(LoadContext<?, ?, ?> context) {
		render((T) context.getResult());
		StateAble stateAble=getStateAble();
		if(stateAble!=null)
			stateAble.showNormal();
	}
	
	@Override
	public void loadFail(com.qad.loader.LoadContext<?,?,?> context) {
		StateAble stateAble=getStateAble();
		if(stateAble!=null)
			stateAble.showRetryView();
	}
	
	@Override
	public void onRetry(View view) {
		startLoading();
	}
	
}
