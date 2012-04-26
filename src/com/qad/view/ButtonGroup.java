package com.qad.view;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class ButtonGroup implements OnTouchListener{
	final View[] views;
	int currentSelected=0;
	OnSelectChangeListener listener;
	
	public static interface OnSelectChangeListener
	{
		void onSelectViewChange(View view);
	}
	
	public ButtonGroup(View... views)
	{
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
			listener.onSelectViewChange(views[currentSelected]);
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
