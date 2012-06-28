package com.qad.form;

/**
 * PageLoader是一个分页助手类。该类主要实现了如何加载页，更新页数据和分页总数信息。<br>
 * 并负责返回一个PageManager的公开对象，以便外界操纵翻页操作。
 * @author 13leaf
 *
 */
public interface PageLoader<Content> {
	
	/**
	 * 未载入状态，默认设置
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
	 * 在loadPage结束,如线程结束时,必须对PageManager进行一次唤醒更新。注,同步模式也需要进行唤醒更新。<br>
	 * 同步模式在有些情况很有用，比如当对内存中的列表等集合进行操作的时候。
	 * 务必通知PageAble开始载入
	 * @param pageNo
	 * @param pageSize
	 * @return 是否同步或异步。返回false表示是异步，返回true表示是同步。
	 */
	boolean loadPage(int pageNo,int pageSize);
		
	/**
	 * 对外公开的分页器对象。可以使用分页器来操作PageAble实例进行翻页操作。
	 * @return
	 */
	PageManager<Content> getPager();
}
