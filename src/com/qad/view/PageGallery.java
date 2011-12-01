package com.qad.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Gallery;

/**
 * 实现了一页一页平滑翻动(不带flying)的画廊。<br>
 * 重写了onFling导致的Gallery翻页过快问题。
 * FIXME 滑动过程中有卡壳的现象
 * @author 13leaf
 * 
 */
public class PageGallery extends Gallery {

	private boolean scrollLeftAble = true;

	private boolean scrollRightAble = true;

	private float downX = 0;

	private ScrollDelegate mScrollDelegate;

	public interface ScrollDelegate {
		void onScrollLeft(boolean scrollLeftAble);

		void onScrollRight(boolean scrollRight);
	}

	public void setOnScrollDelegate(ScrollDelegate mDelegate) {
		mScrollDelegate = mDelegate;
	}

	public PageGallery(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public PageGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public PageGallery(Context context) {
		super(context);
		init();
	}

	private void init() {
		setHorizontalFadingEdgeEnabled(false);
		setVerticalFadingEdgeEnabled(false);
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		int keyEvent;
		if (e1 == null || e2 == null)
			return true;// XXX sometimes that happend,but I don't know why
		if (e2.getX() > e1.getX())// to left
			keyEvent = KeyEvent.KEYCODE_DPAD_LEFT;
		else
			keyEvent = KeyEvent.KEYCODE_DPAD_RIGHT;
		onKeyDown(keyEvent, null);
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT && !scrollLeftAble) {
			return true;// disable scroll left
		}
		if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT && !scrollRightAble) {
			return true;// disable scroll right
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			downX = event.getX();
		}

		if (event.getAction() == MotionEvent.ACTION_MOVE) {
			if (event.getX() > downX)// to left
			{
				if (!scrollLeftAble)
					return true;// disable
			}
			if (event.getX() < downX)// to right
			{
				if (!scrollRightAble)
					return true;
			}
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			if (event.getX() > downX) {
				if (mScrollDelegate != null)
					mScrollDelegate.onScrollLeft(scrollLeftAble);
			}
			if (event.getX() < downX) {
				if (mScrollDelegate != null)
					mScrollDelegate.onScrollRight(scrollRightAble);
			}
		}
		return super.onTouchEvent(event);
	}

	/**
	 * @return the scrollLeftAble
	 */
	public boolean isScrollLeftAble() {
		return scrollLeftAble;
	}

	/**
	 * @param scrollLeftAble
	 *            the scrollLeftAble to set
	 */
	public void setScrollLeftAble(boolean scrollLeftAble) {
		this.scrollLeftAble = scrollLeftAble;
	}

	/**
	 * @return the scrollRightAble
	 */
	public boolean isScrollRightAble() {
		return scrollRightAble;
	}

	/**
	 * @param scrollRightAble
	 *            the scrollRightAble to set
	 */
	public void setScrollRightAble(boolean scrollRightAble) {
		this.scrollRightAble = scrollRightAble;
	}

	/**
	 * 锁住,不允许滚动
	 */
	public void lock() {
		scrollLeftAble = false;
		scrollRightAble = false;
	}

	/**
	 * 解锁,允许滚动
	 */
	public void unLock() {
		scrollLeftAble = true;
		scrollRightAble = true;
	}

}
