package com.qad.form;

/**
 * 包含载入页信息的简单POJO类
 * @author 13leaf
 *
 */
public class SimpleLoadContent<Content> {

	public SimpleLoadContent()
	{
		
	}
	
	public SimpleLoadContent(int pageNo, int pageSize, int pageSum,
			Content content) {
		super();
		this.pageNo = pageNo;
		this.pageSize = pageSize;
		this.pageSum = pageSum;
		this.content = content;
	}

	private int pageNo;
	
	private int pageSize;
	
	private int pageSum;
	
	private Content content;

	/**
	 * @return the pageNo
	 */
	public int getPageNo() {
		return pageNo;
	}

	/**
	 * @param pageNo the pageNo to set
	 */
	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	/**
	 * @return the pageSize
	 */
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * @param pageSize the pageSize to set
	 */
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	/**
	 * @return the pageSum
	 */
	public int getPageSum() {
		return pageSum;
	}

	/**
	 * @param pageSum the pageSum to set
	 */
	public void setPageSum(int pageSum) {
		this.pageSum = pageSum;
	}

	/**
	 * @return the content
	 */
	public Content getContent() {
		return content;
	}

	/**
	 * @param content the content to set
	 */
	public void setContent(Content content) {
		this.content = content;
	}
}
