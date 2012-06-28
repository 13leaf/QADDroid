package com.qad.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.graphics.drawable.Drawable;

/**
 * 一个创建对话框的简单工具。可以用来创建询问对话框和消息提示对话框两种类型。
 * @author 13leaf
 *
 */
public class DialogTool {
	private Context base;
	
	/**
	 * 对话框确认按钮的文本
	 */
	public static final String DEFAULT_POSITVE_TEXT="确定";
	
	/**
	 * 对话框取消按钮的文本
	 */
	public static final String DEFAULT_NEGATIVE_TEXT="取消";
	
	public DialogTool(Context context)
	{
		base=context;
	}
	
	/**
	 * 创建一个包含确定，取消的询问式对话框
	 * @param title
	 * @param icon
	 * @param message
	 * @param yesButton
	 * @param yesListener
	 * @param noButton
	 * @param noListener
	 * @return
	 */
	public AlertDialog createConfirmDialog(String title,Drawable icon,String message,
			String yesButton,OnClickListener yesListener,String noButton,OnClickListener noListener)
	{
		AlertDialog.Builder builder=new AlertDialog.Builder(base);
		if(title!=null) builder.setTitle(title);
		if(icon!=null) builder.setIcon(icon);
		if(noButton!=null) builder.setNegativeButton(noButton, noListener);
		//Message,PositiveButton都是必要创建的
		AlertDialog dialog=
							builder
								.setMessage(message)
								.setPositiveButton(yesButton,yesListener)
								.create();
		return dialog;
			
	}
	
	/**
	 * 创建一个消息对话框 
	 * @param title
	 * @param icon
	 * @param message
	 * @param okButton
	 * @return
	 */
	public AlertDialog createMessageDialog(String title,Drawable icon,String message,
			String okButton)
	{
		return createConfirmDialog(title, icon, message,
				okButton, null, null,null);
	}
	
	/**
	 * 创建一个无标题的消息提示对话框。
	 * @param message
	 * @return
	 */
	public AlertDialog createNoTitleMessageDialog(String message)
	{
		return createMessageDialog(null, null, message, DEFAULT_POSITVE_TEXT);
	}
	
	/**
	 * 创建一个无标题的确认对话框
	 * @param message
	 * @param yesButton
	 * @param yesListener
	 * @param noButton
	 * @param noListener
	 * @return
	 */
	public AlertDialog createNoTitleConfirmDialog(String message,String yesButton,OnClickListener yesListener,String noButton,OnClickListener noListener)
	{
		return createConfirmDialog(null,null,message, 
				yesButton, yesListener, noButton, noListener);
	}
	
	/**
	 * 创建一个简单的确认对话框。没有标题，PositiveButton和NegativeButton使用默认的确认，取消字符串。
	 * 取消按钮默认是关闭。
	 * @param message
	 * @param yesListener
	 * @return
	 */
	public AlertDialog createNoTitleConfirmDialog(String message,OnClickListener yesListener)
	{
		return createNoTitleConfirmDialog(message, 
				DEFAULT_POSITVE_TEXT,yesListener,DEFAULT_NEGATIVE_TEXT,null);
	}
}
