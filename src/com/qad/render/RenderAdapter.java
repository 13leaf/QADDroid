package com.qad.render;

import java.lang.ref.WeakReference;
import java.util.List;

import com.qad.loader.ImageLoader;
import com.qad.loader.ImageLoader.ImageDisplayer;
import com.qad.view.PageListView.PageAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

@SuppressWarnings("rawtypes")
public class RenderAdapter extends BaseAdapter implements PageAdapter{

	private WeakReference<ImageLoader> loaderRef;
	private int layout;
	private List<?> data;
	private ImageDisplayer displayer;

	public RenderAdapter(List<?> data, int layout, ImageLoader loader,ImageDisplayer displayer) {
		this.data = data;
		this.layout = layout;
		this.loaderRef = new WeakReference<ImageLoader>(loader);
		this.displayer=displayer;
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
			convertView = LayoutInflater.from(parent.getContext())
					.inflate(layout, null);
		}
		RenderEngine.render(convertView, getItem(position), loaderRef.get(),displayer);
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
