package com.qad.view;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/**
 * 构造一个单选组。在setSelected后会自动更改View的selected状态,副作用是其会覆写onTouch事件。
 * @author 13leaf
 *
 */
public class ButtonGroup implements OnTouchListener{
	View[] views;
	int currentSelected=0;
	OnSelectChangeListener listener;
	
	public static interface OnSelectChangeListener
	{
		void onSelectViewChange(int index,View view);
	}
	
	//do nothing
	public ButtonGroup()
	{
		
	}
	
	public ButtonGroup(View... views)
	{
		setViews(views);
	}

	public void setViews(View... views) {
		this.views=views;
		for(View view:views)
			view.setOnTouchListener(this);
		invalidateSelect();
	}
	
	public void setOnSelectedChangeListener(OnSelectChangeListener listener)
	{
		this.listener=listener;
	}
	
	public void setSelected(int index)
	{
		if(index>=0 && index<views.length && index!=currentSelected)
		{
			currentSelected=index;
			invalidateSelect();
		}
	}
	
	public void setSelected(View view)
	{
		int index=indexOfView(view);
		if(index==currentSelected) return;
		if(index!=-1){
			currentSelected=index;
			invalidateSelect();
		}
		
	}
	
	private void invalidateSelect() {
		for(View view:views)
			view.setSelected(false);
		views[currentSelected].setSelected(true);
		if(listener!=null)
			listener.onSelectViewChange(currentSelected,views[currentSelected]);
	}

	public int getCurrentSelectedIndex() {
		return currentSelected;
	}
	
	public View getCurrentSelectedView(){
		return views[currentSelected];
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		
		if(event.getAction()==MotionEvent.ACTION_DOWN)
		{
			setSelected(v);
		}
		
		return false;
	}

	private int indexOfView(View v) {
		for (int i = 0; i < views.length; i++) {
			if(views[i]==v)
				return i;
		}
		return -1;
	}
}
