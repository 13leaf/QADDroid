package com.qad.demo.group;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;

import com.qad.app.BaseActivityGroup;
import com.qad.form.MasterActivity.onNavigateListener;

/**
 * 母版Activity。其母版布局文件中必须包含一个以content为id的layout文件。<br>
 * 默认情况下母版Activity会在首次调用内容Activity时创建它，之后直接跳转到已存在的实例。<br>
 * 因此母版Activity中嵌套的内容Activity应该尽量在onResume方法中响应可能发生更改的地方。<br>
 * 如若希望母版Activity不要在跳转时保存内容Activity实例，换言之每次跳转都是重新create
 * Activity，那么请设置createOnce为false即可。
 * 
 * @author 13leaf
 * 
 */
public class TestMasterActivity extends BaseActivityGroup {

	/**
	 * 内容ViewGroup要求的id名称
	 */
	public static final String CONTENT_ID_NAME = "content";

	private static final String STATE_CURRENT_NAVIGATEID = "currentNavigateID";
	
	private static final String STATE_CURRENT_NAVIGATE_INTENT="currentNavigateIntent";

	private LocalActivityManager mLocalActivityManager;

	private boolean createOnce = false;// 只创建一次内容Activity，之后跳转不重新创建

	private ViewGroup contentViewGroup;// 内容视图

	private View currentNavigatorView;// 获取当前指示焦点的目标
	
	private String currentNavigateId;
	
	private Intent currentNavigateIntent;

	private onNavigateListener mNavigateListener = null;
	
	private ArrayList<View> navViews=new ArrayList<View>();
	/**
	 * 一个HashMap,存储当前绑定的映射关系
	 */
	protected HashMap<String, NavigateEntry> entryMap = new HashMap<String, NavigateEntry>();

	/**
	 * 发生导航时的监听器
	 * 
	 * @author 13leaf
	 * 
	 */
	public interface onNavigateListener {
		/**
		 * 当导航发生时被调用
		 * 
		 * @param tag
		 * @param intent
		 * @return 是否继续处理导航。若返回true，将不处理导航
		 */
		boolean onNavigation(String tag,Intent intent);
	}

	protected static class NavigateEntry {
		public NavigateEntry(String contentId, Intent contentIntent,
				View rootView) {
			this.contentId = contentId;
			this.contentIntent = contentIntent;
			this.rootView = new WeakReference<View>(rootView);
		}

		public final String contentId;
		public final Intent contentIntent;
		public final WeakReference<View> rootView;
	}
	
	private ViewGroup getContentViewGroup() {
		if (contentViewGroup == null) {
			contentViewGroup = (ViewGroup) findViewByIdName(CONTENT_ID_NAME);
			if (contentViewGroup == null)
				throw new RuntimeException(
						"请确保master的layout中设置了id为content的布局!或者确保在onCreate中调用了setContentView方法!");
		}
		return contentViewGroup;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// save current navigate
		outState.putString(STATE_CURRENT_NAVIGATEID, currentNavigateId);
		outState.putParcelable(STATE_CURRENT_NAVIGATE_INTENT, currentNavigateIntent);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		if(savedInstanceState!=null)
		{
			String restoreId=savedInstanceState.getString(STATE_CURRENT_NAVIGATEID);
			Intent restoreIntent=savedInstanceState.getParcelable(STATE_CURRENT_NAVIGATE_INTENT);
			navigate(restoreId,restoreIntent);
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mLocalActivityManager=getLocalActivityManager();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if(currentNavigateId==null && navViews.size()!=0)
		{
			navViews.get(0).performClick();//hack a mock click event
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		navViews.clear();
		entryMap.clear();
		contentViewGroup=null;
	}
	

	/**
	 * 将导航的视图绑定到某个Activity中。点击导航视图会切换content。若希望设置绑定时立即呈现，请调用重载函数<br>
	 * 必须在setContentView之后调用此方法。<br>
	 * <strong>建议不要将按钮重新绑定某个活动，否则绑定前启动的活动将持续占用资源。</strong>
	 * @param navView 引起链接行为的导航View
	 * @param intent 导航Activity目标的Intent
	 * @param tag	标识符
	 * @param current 是否当前立即显示
	 */
	public void bindNavigate(View navView, final Intent intent, final String tag,
			boolean current) {
		if(current){
			currentNavigatorView=navView;
			navigate(tag, intent);
		}
		navViews.add(navView);
		navView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//ensure selected
				if(currentNavigatorView!=null){
					currentNavigatorView.setSelected(false);
				}
				v.setSelected(true);
				currentNavigatorView=v;
				navigate(tag, intent);
			}
		});
	}
	
	public void bindNavigate(int id, Class<? extends Activity> contentActivity,
			String tag,boolean current){
		bindNavigate(findViewById(id), getLocalIntent(contentActivity),tag,current);
	}
	
	public void bindNavigate(int id, Class<? extends Activity> contentActivity,
			String tag){
		bindNavigate(findViewById(id), getLocalIntent(contentActivity), tag, false);
	}

	public void bindNavigate(View navView, String tag, Intent intent) {
		bindNavigate(navView, intent, tag, false);
	}

	/**
	 * 
	 * @param navView
	 */
	public void unBindNavigate(View navView) {
		navView.setOnClickListener(null);
		navViews.remove(navView);
	}
	
	/**
	 * 设置Navigate监听器。以此可以在导航发生之前捕获
	 * 
	 * @param listener
	 */
	public void setOnNavigateListener(onNavigateListener listener) {
		mNavigateListener = listener;
	}

	/**
	 * 返回启动后对应Activity的根View
	 * @param intent
	 * @param tag
	 * @return
	 */
	private View start(Intent intent,String tag) {
		View decorView=mLocalActivityManager.startActivity(tag, intent).getDecorView();
		if(createOnce){
			mLocalActivityManager.removeAllActivities();//强制重建
			entryMap.clear();
		}
		return decorView;
	}
	
	public void navigate(String tag)
	{
		NavigateEntry entry=entryMap.get(tag);
		if(entry==null) throw new NullPointerException("Have you ever bindNavigator?");
		navigate(entry.contentId,entry.contentIntent);
	}

	/**
	 * 链接到指定Intent的Activity视图。其中tag将作为标识符
	 * @param navViewID
	 */
	public void navigate(String tag,Intent intent) {
		if(intent==null || tag==null || tag.length()==0) throw new NullPointerException();
		if(currentNavigateId!=null && currentNavigateId.equals(tag)) return;
		View contentView=null;
		if(!entryMap.containsKey(tag))
		{
			contentView=start(intent, tag);
			NavigateEntry entry=new NavigateEntry(tag, intent, contentView);
			entryMap.put(tag, entry);
		}else {
			contentView=entryMap.get(tag).rootView.get();
		}
		getContentViewGroup().removeAllViews();
		getContentViewGroup().addView(contentView);
		contentView.setFocusableInTouchMode(true);contentView.setFocusable(true);
		contentView.requestFocus();
		if(mNavigateListener!=null)
		{
			mNavigateListener.onNavigation(tag, intent);
		}
		currentNavigateId=tag;
		currentNavigateIntent=intent;
	}
	
	long latestDownTime;
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		testLog(event.getDownTime()-latestDownTime);
		latestDownTime=event.getDownTime();
		return super.dispatchKeyEvent(event);
	}

	// 修复由于使用ActivityGroup产生子Activity之间菜单无法打开的问题。
	//FIXME 仅能在首次创建时成功执行,之后都会出现问题
//	@Override
//	public boolean dispatchKeyEvent(KeyEvent event) {
//		// 传递子类的menu事件
//		switch (event.getKeyCode()) {
//		case KeyEvent.KEYCODE_MENU:
//			runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
//					mLocalActivityManager.getCurrentActivity().openOptionsMenu();
//				}
//			});
//			return true;// handled
//		default:
//			return super.dispatchKeyEvent(event);
//		}
//	}

	public boolean isCreateOnce() {
		return createOnce;
	}

	/**
	 * 默认情况下createOnce是开启的。当设置createOnce为false的时候。每次跳转内容,都会重建一次Activity。<br>
	 * 请尽量在发生跳转前设置好此属性。
	 * 
	 * @param createOnce
	 *            the createOnce to set
	 */
	public void setCreateOnce(boolean createOnce) {
		this.createOnce = createOnce;
	}

	/**
	 * 获得当前被选中的navigatorView(假如调用bindNavigator绑定后)
	 * 
	 * @return the currentNavigatorView
	 */
	public View getCurrentNavigatorView() {
		return currentNavigatorView;
	}
}