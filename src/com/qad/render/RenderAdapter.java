package com.qad.render;

import java.lang.ref.WeakReference;
import java.util.List;

import com.qad.loader.ImageLoader;
import com.qad.loader.ImageLoader.ImageDisplayer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class RenderAdapter extends BaseAdapter {

	private WeakReference<ImageLoader> loaderRef;
	private int layout;
	private List<?> data;
	private String namespace;
	private ImageDisplayer displayer;

	public RenderAdapter(List<?> data, int layout, String namespace,ImageLoader loader,ImageDisplayer displayer) {
		this.data = data;
		this.namespace=namespace;
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
		RenderEngine.render(convertView, getItem(position), namespace, loaderRef.get(),displayer);
		return convertView;
	}
}
