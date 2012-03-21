package com.qad.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.qad.form.PageManager;
import com.qad.form.PageManager.PageLoadListener;

/**
 * 若想让PageListView提供了两种翻页形式。AUTO_MODE表示当页滑动到页低部时自动翻页。MANUAL_MODE表示当页滑动到页<br>
 * 底端时。显示一个按钮给用户进行手动翻页。当翻页加载时，列表底部显示一个滚动等待的信息。当加载失败的时候，会<br>
 * 显示一个Toast的提示信息。显示的这些信息可以通过设置loadButton,loadingMsg,loadError来灵活订制。<br>
 * 注:在xml中需要申明:mode="auto|manual",另外可设置的属性有loadingMsg,loadingErrorMsg,loadButtonText.使用PageListView时务必需要通过bindPageManager来完成绑定。
 * @author 13leaf
 *
 */
public  class PageListView extends ListView {

	@SuppressWarnings("rawtypes")
	private PageManager mPageManager;
	
	//FIXED 允许子类控制footer view,以此来增加灵活性。
	/**
	 * loadSwitcher在初始构造的时候被添加。你可以自行控制loadSwitcher的remove和add操作。因为android的listAdapter很不优美的实现bug。。。
	 * */
	protected ViewSwitcher loadSwitcher;
	
	protected  TextView txtLoading;
	
	protected Button btnLoad;
	
	private boolean showFlag=true;//ViewSwitcher的标志位，true表示显示加载中,false表示显示加载按钮.
	
	private int mFlag=0;//选择的标志模式
	
	private boolean loadFail;//这个标志是为了控制AUTO_MODE，让其在载入失败后不再自动滚动刷新
	
	@SuppressWarnings("rawtypes")
	private PageAdapter adapter;
	
	/**
	 * 手动载入的Button内容
	 */
	public String loadButton="查看更多内容";
	
	/**
	 * 载入尾部显示信息
	 */
	public String loadingMsg="正在载入,请稍后...";
	
	/**
	 * 加载失败的提示信息
	 */
	public String loadErrorMsg="加载失败,请重试...";
	
	/**
	 * 自动翻页，当列表滚动到最后的时候触发翻页事件。
	 */
	public static final int AUTO_MODE=0;
	
	/**
	 * 手动翻页，当列表滚动到最后的时候显示一个载入的Button。点击Button后翻页，若到达页尾部，Button会自动隐藏.
	 */
	public static final int MANUAL_MODE=1;
	
	/**
	 * PageAdapter的addPage方法用于添加页数据到列表。<br>
	 * PageContent表示页加载数据的类型，其类型应当与构造函数中的PageManager相一致
	 * @author 13leaf
	 *
	 */
	public interface PageAdapter<PageContent> extends ListAdapter
	{
		void addPage(PageContent pageContent);
	}
	
	/**
	 * 可通过bindPageManager进行延迟绑定
	 * @param context
	 * @param pageManager 可空
	 * @param flag
	 */
	public PageListView(Context context,PageManager<?> pageManager,int flag) {
		super(context);
		
		mPageManager=pageManager;
		//FIXED v2恢复原先在构造函数中初始化footer的规则，子类应手动调用loadSwitcher来自行控制
		initLoadFooter();
		addFooterView(loadSwitcher);
		//FIXED v1设置triggerMode应当提前
		setTriggerMode(flag);
		if(pageManager!=null)
			bindPageManager(mPageManager);
	}
	
	
	
	/**
	 * 从xml配置文件中构建
	 * @param context
	 * @param attrs
	 */
	public PageListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		//set flag mode
		String mode=attrs.getAttributeValue(null, "mode");//default for auto
		if(mode==null) setTriggerMode(AUTO_MODE);
		else if(mode.equals("auto")) setTriggerMode(AUTO_MODE);
		else if(mode.equals("manual")) setTriggerMode(MANUAL_MODE);
		else setTriggerMode(-1);//invalid
		//FIXED v2恢复了原先在够走啊函数中初始化footer的规则
		initLoadFooter();
		addFooterView(loadSwitcher);
		
		//FIXED v1 未设置的时候使用默认名称
		//set load
		String loadButtonText=attrs.getAttributeValue(null, "loadButtonText");
		String loadErrorText=attrs.getAttributeValue(null,"loadErrorMsg");
		String loadingText=attrs.getAttributeValue(null,"loadingMsg");
		if(loadButtonText!=null)
			loadButton=loadButtonText;
		if(loadErrorText!=null)
			loadErrorMsg=loadErrorText;
		if(loadingText!=null)
			loadingMsg=loadingText;
	}
	

	/**
	 * 可通过bindPageManager进行延迟绑定
	 * @param context
	 * @param pageManager 可空
	 */
	public PageListView(Context context,PageManager<?> pageManager)
	{
		this(context, pageManager, AUTO_MODE);
	}
	
	
	/**
	 * 将PageListView与PageManager绑定
	 * issue:pageManager不带泛型
	 */
	public void bindPageManager(PageManager<?> pageManager)
	{
		mPageManager=pageManager;
		mPageManager.addOnPageLoadListioner(new ListPageLoad());
		//
		mPageManager.bindNext(btnLoad);
		//
		if(mFlag==AUTO_MODE)
		{
			setOnScrollListener(new AutoLoadScrollListener());
		}
		else if(mFlag==MANUAL_MODE)
			showLoadedFooter();//初始化为开始载入状态
		else
			throw new RuntimeException("Flag设置不正确!");
		
		mPageManager.next();//begin init
	}
	
	/**
	 * 重设当前的翻页触发器。
	 * 应当为Manual或AUTO
	 * @param mode
	 */
	public void setTriggerMode(int mode)
	{
		if(mFlag!=mode){
			mFlag=mode;
			if(mFlag==MANUAL_MODE){
				showLoadedFooter();
				setOnScrollListener(null);//屏蔽自动翻页
			}else if(mFlag==AUTO_MODE){
				setOnScrollListener(new AutoLoadScrollListener());
			}else{
				throw new RuntimeException("Flag设置不正确!");
			}
		}
	}
	
	
	/**
	 * 返回绑定的PageManager
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public PageManager getPageManager()
	{
		return mPageManager;
	}


	/**
	 * 以硬编码的方式来解决底部加载框。目前只能使用这种慵懒的实现手段...
	 */
	private void initLoadFooter()
	{
		loadSwitcher=new ViewSwitcher(getContext());
		//
		loadSwitcher.addView(initLoadingView());
		
		loadSwitcher.addView(initLoadView());
	}
	
	
	/**
	 * 子类覆盖此方法的时候必须要手动设置txtLoading
	 * @return
	 */
	protected View initLoadingView() {
		LinearLayout loadingLayout=new LinearLayout(getContext());
		loadingLayout.setGravity(Gravity.CENTER);loadingLayout.setOrientation(LinearLayout.HORIZONTAL);
		ProgressBar progressBar=new ProgressBar(getContext());
		progressBar.setIndeterminate(true);
		loadingLayout.addView(progressBar);
		txtLoading=new TextView(getContext());
		txtLoading.setGravity(Gravity.CENTER);
		txtLoading.setSingleLine(true);
		txtLoading.setTextSize(17);
		txtLoading.setPadding(12, 0, 0, 0);
		loadingLayout.addView(txtLoading);
		return loadingLayout;
	}
	/**
	 * 子类覆盖此方法的时候必须手动设置btnLoad
	 * @return
	 */
	protected View initLoadView(){
		btnLoad=new Button(getContext());
		btnLoad.setGravity(Gravity.CENTER);
		btnLoad.setTextSize(17);
		return btnLoad;
	}
	
	/**
	 * 当列表到达尾端直接翻页
	 * @author 13leaf
	 *
	 */
	final class AutoLoadScrollListener implements OnScrollListener
	{

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			if(firstVisibleItem==0 || loadFail) return;
			
			if(firstVisibleItem+visibleItemCount==totalItemCount)//翻页到底部
			{
				mPageManager.next();//触发翻页
			}
		}
		
	}
	
	final class ListPageLoad implements PageLoadListener
	{

		@Override
		public void onPageLoading(int loadPageNo, int pageSum) {
			showLoadingFooter();
		}

		@Override
		public void onPageLoadComplete(int loadPageNo, int pageSum,
				Object content) {
			updateList(content);
			//
			loadFail=false;
			//已经是最后一页了
			if(loadPageNo>=pageSum)
			{
				removeFooterView(loadSwitcher);
			}
			//重新设置状态
			if(mFlag==AUTO_MODE)
				showLoadingFooter();
			else if(mFlag==MANUAL_MODE)
				 	showLoadedFooter();
		}

		@Override
		public void onPageLoadFail(int loadPageNo, int pageSum) {
			Toast.makeText(getContext(), loadErrorMsg,Toast.LENGTH_SHORT).show();
			loadFail=true;
			showLoadedFooter();
		}
		
	}
	
	/**
	 * 更新列表，将读到的Page数据添加到列表中去。
	 * @param currentContent
	 */
	@SuppressWarnings("unchecked")
	protected void updateList(Object currentContent)
	{
		if(adapter!=null)
			adapter.addPage(currentContent);
	}
	
	/**
	 * 必须设置支持PageAdapter接口的适配器实例
	 * issue:在添加了footerview后,adapter被绑定成了HeaderView。此时getAdapter将总是返回一个HeaderViewAdapter实例
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public void setAdapter(ListAdapter adapter) {
		//FIXED v1.0 当adapter为Null时应当传入,并且将adapter设置为空
		if(adapter==null){
			super.setAdapter(null);
			this.adapter=null;
		}
		else if(adapter instanceof PageAdapter)
		{
			super.setAdapter(adapter);
			this.adapter=(PageAdapter) adapter;
		}else {
			throw new RuntimeException("只支持PageAdapter接口。");
		}
	}
	
	private void showLoadingFooter()
	{
		txtLoading.setText(loadingMsg);
		btnLoad.setVisibility(INVISIBLE);
		if(!showFlag)
		{
			loadSwitcher.showPrevious();
			showFlag=true;
		}
	}
	
	private void showLoadedFooter()
	{
		btnLoad.setText(loadButton);
		btnLoad.setVisibility(VISIBLE);
		if(showFlag)
		{
			loadSwitcher.showNext();
			showFlag=false;
		}
	}

}
