package com.qad.view;

import java.util.ArrayList;
import java.util.List;

import com.qad.form.PageEntity;

public class FallEntries implements PageEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1497156110025527869L;
	
	private int pageSum=Integer.MAX_VALUE;
	private List<FallEntry> entries=new ArrayList<FallEntry>();
	
	public void setEntries(List<FallEntry> entries) {
		this.entries = entries;
	}
	
	public List<FallEntry> getEntries() {
		return entries;
	}
	
	public void setPageSum(int pageSum) {
		this.pageSum = pageSum;
	}
	
	@Override
	public int getPageSum() {
		return pageSum;
	}

	@Override
	public List<?> getData() {
		return entries;
	}

}
