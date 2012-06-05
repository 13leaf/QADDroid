package com.qad.form;

import java.lang.ref.WeakReference;
import java.util.HashMap;

import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.AndroidRuntimeException;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qad.app.BaseActivityGroup;

/**
 * 母版Activity。其母版布局文件中必须包含一个以content为id的layout文件。<br>
 * 如果包含名为contentTitle的TextView控件，则会自动将其与导航过程绑定。
 * 调用bindNavigate中包含title的重载函数即可绑定导航发生时对应的标题名称<br>
 * 默认情况下母版Activity会在首次调用内容Activity时创建它，之后直接跳转到已存在的实例。<br>
 * 因此母版Activity中嵌套的内容Activity应该尽量在onResume方法中响应可能发生更改的地方。<br>
 * 如若希望母版Activity不要在跳转时保存内容Activity实例，换言之每次跳转都是重新create
 * Activity，那么请设置createOnce为false即可。
 * 
 * @author 13leaf
 */
public class MasterActivity extends BaseActivityGroup {

	private LocalActivityManager mLocalActivityManager;// 管理多个内容Activity

	private boolean createOnce = true;// 只创建一次内容Activity，之后跳转不重新创建

	private ViewGroup contentViewGroup;// 内容视图

	private TextView contentTitle = null;// 导航文本

	private boolean hasTitle = true;// 优化标志位,避免重复的findViewbyId操作

	private View currentNavigatorView;// 获取当前指示焦点的目标

	private onNavigateListener mNavigateListener = null;

	/**
	 * 内容ViewGroup要求的id名称
	 */
	public static final String CONTENT_ID_NAME = "content";
	/**
	 * 与内容绑定的Title名称,该Title必须为TextView或其子类
	 */
	public static final String CONTENT_TITILE_ID_NAME = "contentTitle";
	
	private static final String STATE_CURRENT_NAVIGATEID="currentNavigateID";

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
		 * @param navigateView
		 *            发生导航的导航组件
		 * @param title
		 *            绑定的导航标题
		 * @return 是否继续处理导航。若返回true，将不处理导航
		 */
		boolean onNavigation(View navigateView, String title);
	}

	protected final class NavigateEntry {
		public NavigateEntry(String contentId, Intent contentIntent,
				String title, WeakReference<View> navigateView) {
			super();
			this.contentId = contentId;
			this.contentIntent = contentIntent;
			this.title = title;
			this.navigateView = navigateView;
		}

		String contentId;
		Intent contentIntent;
		String title;
		WeakReference<View> navigateView;
	}

	/**
	 * 一个HashMap,存储当前绑定的映射关系
	 */
	protected HashMap<Integer, NavigateEntry> entryMap = new HashMap<Integer, MasterActivity.NavigateEntry>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mLocalActivityManager = getLocalActivityManager();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		//save current navigate
		if(currentNavigatorView!=null)
			outState.putInt(STATE_CURRENT_NAVIGATEID, currentNavigatorView.getId());
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		//FIXME 若调用了bindNavigate包含showNow的重载。则无法跳过第一个启动的活动.加入Default跳转机制
		if(savedInstanceState!=null){
			int currentNavigateID=savedInstanceState.getInt(STATE_CURRENT_NAVIGATEID);
			if(currentNavigateID!=0)
				navigate(currentNavigateID);
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	public View getCurrentNavigateView()
	{
		return currentNavigatorView;
	}

	/**
	 * 将导航的视图绑定到某个Activity中。点击导航视图会切换content。<br>
	 * 必须在setContentView之后调用此方法。<br>
	 * <strong>建议不要将按钮重新绑定某个活动，否则绑定前启动的活动将持续占用资源。</strong>
	 * 
	 * @param navView
	 *            绑定的视图View
	 * @param mIntent
	 * 			intent,可注入参数
	 * @param contentActivity
	 *            内容活动类
	 * @param title
	 *            导航目标的标题。
	 * @param showNow
	 *            是否在绑定时立即呈现
	 */
	public void bindNavigate(View navView, Class<? extends Activity> contentActivity,Intent mIntent,
			final String title, boolean showNow) {
		final Intent intent = new Intent(mIntent);
		intent.setClass(this, contentActivity);
		final String contentId = contentActivity.getCanonicalName();
		final View navigatorView = navView;

		if (showNow) {
			currentNavigatorView = navigatorView;
			currentNavigatorView.setSelected(true);
			showContent(contentId, intent, title);
		}
		// set listener
		navigatorView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// FIXED v1.0 修复重复点击一个navigator引起问题
				if (v == currentNavigatorView)
					return;
				currentNavigatorView.setSelected(false);// 清空上一个选择
				currentNavigatorView = v;
				currentNavigatorView.setSelected(true);// 确保当前选择
				// notify listener
				if (mNavigateListener != null) {
					boolean handled = mNavigateListener.onNavigation(v, title);
					if (handled)
						return;
				}

				showContent(contentId, intent, title);
			}
		});
		entryMap.put(navigatorView.getId(), new NavigateEntry(contentId, intent, title,
				new WeakReference<View>(navigatorView)));
	}
	
	public void bindNavigate(int id, Class<? extends Activity> contentActivity,
			final String title, boolean showNow) {
		bindNavigate(findViewById(id), contentActivity,new Intent(),title,showNow);
	}

		

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// 传递子类的menu事件
		switch (event.getKeyCode()) {
		case KeyEvent.KEYCODE_MENU:
			mLocalActivityManager.getCurrentActivity().openOptionsMenu();
			return true;// handled
		default:
			return super.dispatchKeyEvent(event);
		}
	}

	/**
	 * 将导航的视图绑定到某个Activity中。点击导航视图会切换content。若希望设置绑定时立即呈现，请调用重载函数<br>
	 * 必须在setContentView之后调用此方法。<br>
	 * <strong>建议不要将按钮重新绑定某个活动，否则绑定前启动的活动将持续占用资源。</strong>
	 * 
	 * @param id
	 *            绑定视图的id号
	 * @param title
	 * @param contentActivity
	 *            内容活动类
	 */
	public void bindNavigate(int id, Class<? extends Activity> contentActivity,
			final String title) {
		bindNavigate(id, contentActivity, title, false);
	}

	/**
	 * 无title式绑定
	 * 
	 * @param id
	 * @param contentActivity
	 */
	public void bindNavigate(int id, Class<? extends Activity> contentActivity) {
		bindNavigate(id, contentActivity, null, false);
	}

	/**
	 * 无title式绑定
	 * 
	 * @param id
	 * @param contentActivity
	 * @param showNow
	 */
	public void bindNavigate(int id, Class<? extends Activity> contentActivity,
			boolean showNow) {
		bindNavigate(id, contentActivity, null, showNow);
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
	 * 启动内容活动，并且显示其内容
	 * 
	 * @param id
	 * @param intent
	 * @param title
	 *            内容标题,若无标题。可设置为空
	 */
	private void showContent(String contentId, Intent intent, String title) {
		testLog("contentId:" + contentId);
		if (contentViewGroup == null) {
			contentViewGroup = (ViewGroup) findViewByIdName(CONTENT_ID_NAME);
			if (contentViewGroup == null)
				throw new RuntimeException(
						"请确保master的layout中设置了id为content的布局!或者确保在onCreate中调用了setContentView方法!");
		}
		// set title
		if (hasTitle) {
			if (contentTitle == null) {
				contentTitle = (TextView) findViewByIdName(CONTENT_TITILE_ID_NAME);
				if (contentTitle == null)
					hasTitle = false;
				else
					contentTitle.setText(title);
			} else {
				contentTitle.setText(title);
			}
		}

		if (!createOnce)
			mLocalActivityManager.removeAllActivities();// 重建
		
		Activity activity=mLocalActivityManager.getActivity(contentId);
		View contentView=null;
		if(activity==null || !activity.getIntent().equals(intent)){
			contentView = mLocalActivityManager.startActivity(contentId,intent).getDecorView();
		}else {
			contentView=activity.getWindow().getDecorView();
		}

		contentViewGroup.removeAllViews();
		contentViewGroup.addView(contentView);
	}

	/**
	 * 请求导航
	 * 
	 * @param navigateID
	 */
	public void navigate(int navigateID) {
		if (entryMap.containsKey(navigateID)) {
			NavigateEntry entry = entryMap.get(navigateID);
			showContent(entry.contentId, entry.contentIntent, entry.title);
			if (entry.navigateView.get() != null
					&& entry.navigateView.get() != currentNavigatorView) {
				if(currentNavigatorView!=null)//ensure navigate has been initialize
					currentNavigatorView.setSelected(false);// cancel select
				//
				currentNavigatorView = entry.navigateView.get();
				//
				currentNavigatorView.setSelected(true);// select it
			}
		} else {
			throw new AndroidRuntimeException(
					"未绑定注册该id的Navigate,请确保bindNavigate");
		}
	}

	/**
	 * @return the createOnce
	 */
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
