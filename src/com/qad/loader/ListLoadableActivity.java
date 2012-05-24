package com.qad.loader;

import android.view.View;

import com.qad.form.PageEntity;
import com.qad.form.PageLoader;
import com.qad.form.PageManager;

/**
 * 本类应当与PageListView配合使用。子类需要实现loadPage以来完成其加载任务。<br>
 * 与LoadableActivity一样，可以通过实现getStateAble做到状态视图的自动切换。<br>
 * 略有不同之处在于，仅在第一次请求失败才切换至失败状态视图。之后的成功失败将由PageListView自身负责。
 */
public abstract class ListLoadableActivity<T extends PageEntity> extends LoadableActivity<T> implements PageLoader<T>{
	
	private PageManager<T> pager;
	private boolean firstLoad=true;
	private int pageSum;
	protected int loadNo;
	protected int pageSize=20;

	@Override
	public void startLoading() {
		throw new UnsupportedOperationException();
	}
	
	public void render(T data) {
		throw new UnsupportedOperationException();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void loadComplete(LoadContext<?, ?, ?> context) {
		if(firstLoad)
		{
			firstLoad=false;
		}
		PageEntity entity=(PageEntity) context.getResult();
		pageSum=entity.getPageSum();
		getPager().notifyPageLoad(LOAD_COMPLETE, loadNo+1, pageSum,(T)entity);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void loadFail(LoadContext<?, ?, ?> context) {
		//仅当首页加载失败时才做状态切换
		if(firstLoad && getStateAble()!=null){
			getStateAble().showRetryView();
		}
		getPager().notifyPageLoad(LOAD_FAIL, loadNo,pageSum , (T)context.getResult());
	}
	
	@Override
	public void onRetry(View view) {
		loadPage(1, pageSize);
	}
	
	@Override
	public PageManager<T> getPager() {
		if(pager==null)
		{
			pager=new PageManager<T>(this, pageSize);
		}
		return null;
	}
	
}
