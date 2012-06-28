package com.qad.render;

import java.lang.ref.WeakReference;
import java.util.List;

import android.view.View;
import android.view.ViewGroup;
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
		return data.size();
	}

	public List<?> getData() {
		return data;
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
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
}
