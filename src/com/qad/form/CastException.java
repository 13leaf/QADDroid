package com.qad.form;

public class CastException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -128145676756177785L;

	/**
	 * 包装一个Exception
	 */
	public  CastException(Exception ex)
	{
		super(ex);
	}
}
