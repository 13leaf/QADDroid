package com.qad.view;

import android.content.Context;
import android.graphics.Rect;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 单行滚动显示TextView,它弥补了普通TextView默认只能在获得焦点情况下滚动的缺陷
 * @author 13leaf
 *
 */
public class MarqueeTextView extends TextView {

	public MarqueeTextView(Context context) {
		this(context, null);
		init();
	}

	public MarqueeTextView(Context context, AttributeSet attrs) {
		this(context, attrs, android.R.attr.textViewStyle);
		init();
	}
	
	private void init() {
		setSingleLine();
		setEllipsize(TruncateAt.MARQUEE);
		setMarqueeRepeatLimit(-1);
	}

	public MarqueeTextView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onFocusChanged(boolean focused, int direction,
			Rect previouslyFocusedRect) {
		if (focused)
			super.onFocusChanged(focused, direction, previouslyFocusedRect);
	}

	@Override
	public void onWindowFocusChanged(boolean focused) {
		if (focused)
			super.onWindowFocusChanged(focused);
	}

	@Override
	public boolean isFocused() {
		return true;
	}
}