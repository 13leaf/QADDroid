package com.qad.form;

import java.util.LinkedList;
import java.util.List;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;

/**
 * 一个简易的分页工具。<br>
 * 使用该分页工具的步骤如下:<br>
 * <ol>
 * <li>实现PageLoader接口，在页载入完成后调用notifyPageLoad来通知PageManager刷新页码信息</li>
 * <li>为PageLoader建立一个PageManager的实例对象，并通过getPager对外公开。</li>
 * <li>外部程序通过本分页器来实现上下翻页的操作</li>
 * <li>外部程序亦可以通过bindView将控件与分页器绑定。</li>
 * </ol>
 * 现提供了两个翻页监听器来更加灵活的与控件绑定。监听器使用getNext/PreviousHandler可访问。目前这个版本对
 * 分页器的loadState加入了线程访问控制以增强安全性。<br>
 * 另外:PageManager提供了一个无PageLoader的构造器。如不希望实现PageLoader,则可以仅仅通过访问PageManager对象的notifyPageLoad来手动更新。
 * <strong>注意:分页器不是线程安全的。使用时可能有线程冲突。</strong>
 * @author 13leaf
 *
 * @param <Content>
 */
public class PageManager<Content> {
	
	private PageLoader<Content> mPageLoader;
	
	private int pageNo=-1;//当前页码
	
	private int pageLoadSize;//加载页大小
	
	private int pageSum;//可获得的页总数
	
//	private int contentSize;//实际内容大小
	
	private Content content;//内容
	
	private LinkedList<PageLoadListener> loadListioners=new LinkedList<PageManager.PageLoadListener>();
	
	private PageChangeListener changeListener;
	
	private Integer loadState=LOAD_DEFAULT;//载入状态
	
	//////////////////////////////////
	//hideBound用于在边界情况自动隐藏翻页控件
	private boolean hideBound=true;//进入边界自动隐藏
	
	private boolean cycle=false;//循环模式，若开启循环模式，第一页的前页是最后一页，反之最后一页是前页。这在有些时候很有用。

	private View previousView; 
	
	private View nextView;
	
	/**
	 *  未载入状态，默认设置
	 */
	public static final int LOAD_DEFAULT=0x0001;
	/**
	 * 加载中
	 */
	public static final int LOADING=0x0010;
	/**
	 * 加载完成
	 */
	public static final int LOAD_COMPLETE=0x0100;
	/**
	 * 加载超时或失败
	 */
	public static final int LOAD_FAIL=0x1000;
	
	
	/**
	 * 假设分页是从第一页开始的
	 */
	public final static int START_NO=0;
	
	
	public interface PageLoadListener {

		/**
		 * 页加载中。仅异步模式会产生该回调,同步模式将不会产生。
		 * @param manager
		 */
		void onPageLoading(int loadPageNo,int pageSum);
		/**
		 * 页载入成功
		 * @param manager
		 */
		void onPageLoadComplete(int loadPageNo,int pageSum,Object content);
		
		/**
		 * 页载入失败
		 */
		void onPageLoadFail(int loadPageNo,int pageSum);
		
	}
	
	/**
	 * 当页码发生改变时触发
	 * @author 13leaf
	 *
	 */
	public interface PageChangeListener{
		/**
		 * 捕获到跳转页的请求,但并没有完成跳转。此时还在等待Loader载入
		 * @param oldPageNo
		 * @param newPageNo
		 */
		void onPrePageChange(int oldPageNo,int newPageNo);
		
		/**
		 * 跳转页完成,Loader已经载入成功。
		 * @param oldPageNo
		 * @param newPageNo
		 * @param success 标志是否页载入成功
		 */
		void onPostPageChange(int oldPageNo, int pageNo, boolean success);
	}
	
	/**
	 * 构建一个空实现载入功能的Loader类。<br>
	 * 这为异步更新提供了一些灵活性
	 * @author 13leaf
	 *
	 */
	final class NonePageLoader implements PageLoader<Content>
	{
		@Override
		public boolean loadPage(int pageNo, int pageSize) {
			return false;
		}

		@Override
		public PageManager<Content> getPager() {
			return null;
		}
	}
	
	/**
	 * 无Loader的PageManager。当页载入更新时,应当手动触发notifyPageLoad
	 * @param pageLoadSize
	 */
	public PageManager(int pageLoadSize)
	{
		mPageLoader=new NonePageLoader();
		this.pageLoadSize=pageLoadSize;
	}
	
	/**
	 * 请参看PageLoader的描述
	 * @param pageAble
	 * @param pageLoadSize
	 */
	public PageManager(PageLoader<Content> pageAble,int pageLoadSize)
	{
		mPageLoader=pageAble;
		this.pageLoadSize=pageLoadSize;
	}
	
	/**
	 * 创建一个同步载入模式的PageManager对象。该对象与一个List类型的数据源绑定。<br>
	 * @param list
	 * @return
	 */
	public static <Content> PageManager<Content> createPageManager(List<?> list)
	{
		return new ListPageLoader<Content>(list).getPager();
	}
	
	/**
	 * 设置Page Load的监听器
	 */
	public void addOnPageLoadListioner(PageLoadListener listioner)
	{
		if(!loadListioners.contains(listioner))
			loadListioners.add(listioner);
	}
	
	/**
	 * 移除已经注册的监听器
	 * @param listener
	 */
	public void removeOnPageLoadListener(PageLoadListener listener){
		loadListioners.remove(listener);
	}
	
	/**
	 * 设置PageChange的监听器
	 * @param listener
	 */
	public void setOnPageChangeListener(PageChangeListener listener)
	{
		changeListener=listener;
	}
	
	
	/**
	 * 跳页，通知PageLoader进行Load。但是实际页码和页数信息并不立即更新，而是等待PageLoader的异步加载结束后调用notifyPageLoad来通知加载结果。<br>
	 * 如果载入失败，那么页码和页总数信息不会被更新。<br>
	 * 允许跳转到当前页。
	 * @param pageNo
	 * @return 若在边界内，则跳页返回true.否则返回false。若处于loading状态，则不接受其他跳页请求
	 */
	public boolean jump(int jumpNo)
	{
		/*
		 * 事实上,jumpNo始终只是一个请求跳转页的入口。它不发生真正的跳转行为,仅产生请求跳转页和进入载入中状态的回调通知。
		 * 真正改变页状态是在notifyPageLoad中统一完成的。
		 * */
		synchronized (loadState) {
			if ((loadState & LOADING) == LOADING)
				return false;// 不允许加载过程中再次请求翻页

			if (cycle && jumpNo == 0)
				jumpNo=pageSum;
			if (cycle && (jumpNo == pageSum + 1))
				jumpNo = 1;

			if (jumpNo < START_NO)
				return false;// 不允许访问下限跳转

			if (jumpNo <= pageSum || (loadState & LOAD_DEFAULT) == LOAD_DEFAULT) // 若首次载入，或者首次载入失败，页信息尚未初始化，则仍然可以继续尝试载入
			{
				if(changeListener!=null)
					changeListener.onPrePageChange(pageNo, jumpNo);
				boolean sync=
						mPageLoader.loadPage(jumpNo, pageLoadSize);
				//同步模式不需要loading状态。故后面的状态判断统统跳过
				if(sync){
					//始终认为同步模式返回的是true
					return true;
				}
				
				if ((loadState & LOAD_DEFAULT) == LOAD_DEFAULT)// 若是未载入状态，则置入标志位
					loadState = (LOADING | LOAD_DEFAULT);
				else
					loadState = LOADING;

				if (!loadListioners.isEmpty())
				{
					for(PageLoadListener listener : loadListioners)
						listener.onPageLoading(jumpNo, pageSum);
				}
				return true;
			} else {
				return false;
			}
		}
	}
	
	/**
	 * 重设置载入Page的页面大小。
	 * <h3>注意:</h3>重设置后页码处于不被验证的状态，建议在setPageLoadSize后，同时调用first方法来更新。
	 */
	public void setPageLoadSize(int newSize)
	{
		this.pageLoadSize=newSize;
	}
	
	/**
	 * 当PageLoader异步加载完毕时，必须通知PageManager更新加载信息
	 * @param loadState
	 * @param pageSum
	 * @param content
	 */
	public void notifyPageLoad(int loadState,int pageNo,int pageSum,Content content)
	{
		synchronized (this.loadState) {
			int oldPageNo=pageNo;
			if (loadState == LOAD_COMPLETE) {
				this.pageNo = pageNo;
				this.pageSum = pageSum;
				this.content = content;
				this.loadState = LOAD_COMPLETE;
				//
				if (hideBound) {
					// 自动隐藏首页
					if (previousView != null)
						previousView
								.setVisibility(pageNo == START_NO ? View.INVISIBLE
										: View.VISIBLE);
					// 自动隐藏尾页
					if (nextView != null)
						nextView.setVisibility(pageNo == pageSum ? View.INVISIBLE
								: View.VISIBLE);
				}

				if (!loadListioners.isEmpty())
				{
					for(PageLoadListener listener : loadListioners)
						listener.onPageLoadComplete(pageNo, pageSum, content);
				}
				
				if(changeListener!=null)
					changeListener.onPostPageChange(oldPageNo,pageNo,true);
			} else if (loadState == LOAD_FAIL) {
				if ((this.loadState & LOAD_DEFAULT) == LOAD_DEFAULT)// 若首次载入失败，则加入未载入标志
					this.loadState = LOAD_FAIL | LOAD_DEFAULT;
				else
					this.loadState = LOAD_FAIL;
				// don't change current page set
				if (!loadListioners.isEmpty())
				{
					for(PageLoadListener listener : loadListioners)
						listener.onPageLoadFail(pageNo, pageSum);
				}
				if(changeListener!=null)
					changeListener.onPostPageChange(oldPageNo, pageNo,false);
			}
		}
	}
	
	/**
	 * 获得载入情况.可能的值:未载入，正在载入，载入成功，载入失败。
	 * @return
	 */
	public int getLoadState()
	{
		if(loadState==LOAD_DEFAULT) return LOAD_DEFAULT;//下面代码中缺少了loadState为Load_default的考虑
		if((loadState&LOAD_DEFAULT)==LOAD_DEFAULT) return loadState^LOAD_DEFAULT;//若加入了未载入状态，则去除未载入状态信息
		else return loadState;
	}
	
	/**
	 * 返回页信息
	 * @return
	 */
	public Content getContent()
	{
		return content;
	}
	
	public boolean next()
	{
		return jump(pageNo+1);
	}
	
	public boolean previous()
	{
		return jump(pageNo-1);
	}
	
	public boolean first()
	{
		return jump(START_NO);
	}
	
	public boolean last()
	{
		return jump(pageSum);
	}
	/**
	 * @return the pageNo
	 */
	public int getPageNo() {
		return pageNo;
	}

	/**
	 * @return the pageLoadSize
	 */
	public int getPageLoadSize() {
		return pageLoadSize;
	}

	/**
	 * @return the pageSum
	 */
	public int getPageSum() {
		return pageSum;
	}
	
	/**
	 * 将View翻页动作绑定
	 * @param previousView
	 */
	public void bindPrevious(View previousView)
	{
		//尚未初始化时隐藏上一页控件
		if(hideBound && !hasInitialize())
		{
			previousView.setVisibility(View.INVISIBLE);
		}
		this.previousView=previousView;
		
		previousView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				previous();
			}
		});
	}
	
	/**
	 * 将View翻页动作绑定
	 * @param previousView
	 */
	public void bindNext(View nextView)
	{
		this.nextView=nextView;
		
		nextView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				next();
			}
		});
	}
	
	/**
	 * 将View翻页动作绑定
	 * @param previousView
	 */
	public void bindFirst(View firstView)
	{
		firstView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				first();
			}
		});
	}
	
	/**
	 * 将View翻页动作绑定
	 * @param previousView
	 */
	public void bindLast(View lastView)
	{
		lastView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				last();
			}
		});
	}
	
	/**
	 * 重载页面
	 */
	public boolean reload()
	{
		return jump(pageNo);
	}
	
	/**
	 * 判断页信息是否初始化完毕，即首次载入是否已将页下限确定。
	 * @return
	 */
	public boolean hasInitialize()
	{
		return (loadState & LOAD_DEFAULT)!=LOAD_DEFAULT;
	}
	
	/**
	 * 返回是否为第一页。若页当前尚未完成初始化，则返回false。
	 * @return
	 */
	public boolean isFirst()
	{
		if(hasInitialize())
			return pageNo==START_NO;
		else 
			return false;
	}
	
	
	/**
	 * 判断是否为最后一页。若页当前尚未完成初始化，则返回false
	 * @return
	 */
	public boolean isLast()
	{
		if(hasInitialize())
			return pageNo==pageSum;
		else 
			return false;
	}

	/**
	 * 是否开启了边界隐藏功能。若开启了边界隐藏功能，则在翻页到底时隐藏下一页控件，翻页到首时隐藏上一页控件。<br>
	 * 默认的hideBound处于开启状态<br>
	 * 注:若开启了循环模式，则不允许隐藏边界。
	 * @return the hideBound
	 */
	public boolean isHideBound() {
		return hideBound;
	}

	/**
	 * 设置是否开启边界隐藏。默认是开启状态。<br>
	 * 注:若开启了循环模式，则不允许隐藏边界。
	 * @param hideBound the hideBound to set
	 */
	public void setHideBound(boolean hideBound) {
		if(cycle) return;
		this.hideBound = hideBound;
	}
	
	public final class onNextHandler implements OnTouchListener,OnClickListener{
		@Override
		public void onClick(View v) {
			nextView=v;
			next();
		}
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			nextView=v;
			next();
			return false;
		}
	}
	
	public final class onPreviousHandler implements OnTouchListener,OnClickListener{
		@Override
		public void onClick(View v) {
			previousView=v;
			previous();
		}
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			previousView=v;
			previous();
			return false;
		}
	}
	
	private onNextHandler nextHandler;
	private onPreviousHandler previousHandler;
	
	/**
	 * 获得一个下一页的监听处理器
	 * @return
	 */
	public onNextHandler getNextHandler()
	{
		if (nextHandler == null) {
			nextHandler = new onNextHandler();
		}
		return nextHandler;
	}
	
	/**
	 * 获得一个上一页的监听处理器
	 * @return
	 */
	public onPreviousHandler getPreviousHandler()
	{
		if (previousHandler == null) {
			previousHandler= new onPreviousHandler();
		}
		return previousHandler;
	}

	/**
	 * 是否开启了循环跳转。这在有些时候很有用，默认处于关闭状态。
	 * @return the cycle
	 */
	public boolean isCycle() {
		return cycle;
	}

	/**
	 * 设置是否开启了循环跳转。这在有些时候很有用，默认处于关闭状态。
	 * 一旦开启了，则允许在边界进行跳转。将认为最后一页的下一页是第一页。<br>
	 * 开启了cycle标志，将会迫使hideBound为false。
	 * @param cycle the cycle to set
	 */
	public void setCycle(boolean cycle) {
		this.cycle = cycle;
		if(cycle) hideBound=false;
	}
	
}
