package com.qad.demo.loader;

import com.qad.demo.R.id;
import com.qad.demo.R.layout;
import com.qad.loader.ImageLoader;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class BlogListAdapter extends BaseAdapter {

	BlogList list;
	ImageLoader loader;

	public BlogListAdapter(BlogList list,ImageLoader loader) {
		this.list = list;
		this.loader=loader;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(parent.getContext()).inflate(
					layout.loader5, null);
		}
		ImageView thumb=(ImageView) convertView.findViewById(id.thumb);
		loader.startLoading(list.get(position).getThumbnail(),thumb);
		TextView title=(TextView) convertView.findViewById(id.title);
		title.setText(list.get(position).getTitle());
		TextView description=(TextView)convertView.findViewById(id.description);
		description.setText(list.get(position).getDescription());
		return convertView;
	}

}
