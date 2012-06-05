package com.qad.render;

import android.content.Context;
import android.view.View;

public interface ViewFactory {

	View createView(Context context,int position);
	
	void render(View view,Object data,int position);
}
