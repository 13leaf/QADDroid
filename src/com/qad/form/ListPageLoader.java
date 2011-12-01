package com.qad.form;

import java.util.List;

@SuppressWarnings("unchecked")
public class ListPageLoader<Content> implements PageLoader<Content>{

	List<?> list;
	
	PageManager<Content> pager;
	
	public ListPageLoader(List<?> list){
		this.list=list;
		pager=new PageManager<Content>(this,1);
	}
	
	@Override
	public boolean loadPage(int pageNo, int pageSize) {
		pager.notifyPageLoad(LOAD_COMPLETE, pageNo, list.size(), (Content) list.get(pageNo-1));//应用pageNo-1是因为PageManager的页码认为从1开始
		return true;
	}

	@Override
	public PageManager<Content> getPager() {
		return pager;
	}
	
}