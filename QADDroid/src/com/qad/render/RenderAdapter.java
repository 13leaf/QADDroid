package com.qad.render;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import com.qad.loader.ImageLoader;
import com.qad.loader.ImageLoader.ImageDisplayer;
import com.qad.view.PageListView.PageAdapter;

@SuppressWarnings("rawtypes")
public class RenderAdapter extends BaseAdapter implements PageAdapter{

	private WeakReference<ImageLoader> loaderRef;
	private ViewFactory factory;
	private List<?> data;
	private ImageDisplayer displayer;
	
	private ArrayList<View> headerViews=new ArrayList<View>();
	private ArrayList<View> footerViews=new ArrayList<View>();
	
	public static final int ITEM_VIEW_TYPE_HEADER_OR_FOOTER = AdapterView.ITEM_VIEW_TYPE_HEADER_OR_FOOTER;
    public static final int ITEM_VIEW_TYPE_NORMAL = 0;

	public RenderAdapter(List<?> data, ViewFactory factory, ImageLoader loader,ImageDisplayer displayer) {
		this.data = data;
		this.factory=factory;
		this.loaderRef = new WeakReference<ImageLoader>(loader);
		this.displayer=displayer;
	}
	
	public RenderAdapter(List<?> data,int layout, ImageLoader loader,ImageDisplayer displayer) {
		this(data, new DefaultViewFactory(layout), loader, displayer);
	}

	@Override
	public int getCount() {
		return headerViews.size()+footerViews.size()+data.size();
	}

	public List<?> getData() {
		return data;
	}
	
	@Override
	public int getViewTypeCount() {
		return 2;
	}
	
	@Override
	public boolean isEnabled(int position) {
		return getItemViewType(position)!=ITEM_VIEW_TYPE_HEADER_OR_FOOTER;
	}
	
	@Override
	public int getItemViewType(int position) {
		if (position < headerViews.size() || position >= (headerViews.size() + data.size())) {
            return ITEM_VIEW_TYPE_HEADER_OR_FOOTER;
        }

        return ITEM_VIEW_TYPE_NORMAL;
	}

	@Override
	public Object getItem(int position) {
		if(position-headerViews.size()<data.size())
			return data.get(position-headerViews.size());
		else {
			return null;
		}
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(getItemViewType(position)==ITEM_VIEW_TYPE_HEADER_OR_FOOTER){
			if(position<headerViews.size())
				return headerViews.get(position);
			else
				return footerViews.get(position);
		}
		if (convertView == null) {
			convertView = factory.createView(parent.getContext(),position);
		}
		Object data=getItem(position);
		factory.render(convertView, data, position);
		RenderEngine.render(convertView, data, loaderRef.get(),displayer);
		return convertView;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addPage(Object pageContent) {
		if(pageContent instanceof List)
		{
			List list=(List) pageContent;
			if(data!=null){
				data.addAll(list);
				notifyDataSetChanged();
			}
		}
	}
	
	public void addHeaderView(View header){
		if(header!=null && !headerViews.contains(header)){
			headerViews.add(header);
			notifyDataSetChanged();
		}
	}
	
	public void removeHeaderView(View header){
		if(header!=null){
			headerViews.remove(header);
			notifyDataSetChanged();
		}
	}
	
	public void addFooterView(View footer){
		if(footer!=null && !footerViews.contains(footer)){
			footerViews.add(footer);
			notifyDataSetChanged();
		}
	}
	
	public void removeFooterView(View footer){
		if(footer!=null){
			footerViews.remove(footer);
			notifyDataSetChanged();
		}
	}
	
	public boolean containsHeaderView(View header){
		return headerViews.contains(header);
	}

	public boolean containsFooterView(View footer){
		return footerViews.contains(footer);
	}
	
	public int getHeaderViewsCount()
	{
		return headerViews.size();
	}
	
	public int getFooterViewsCount()
	{
		return footerViews.size();
	}
	
	/**
	 * 释放headerView和footerView
	 */
	public void destroy()
	{
		headerViews.clear();
		footerViews.clear();
	}
}
