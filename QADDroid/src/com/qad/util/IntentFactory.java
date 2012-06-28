package com.qad.util;

import java.io.File;

import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.net.Uri;
import android.provider.MediaStore;

/**
 * 封装访问系统自带的组件。
 * @author 13leaf
 *
 */
public class IntentFactory {
	
	/**
	 * 获取一个安装apk文件的Intent
	 * @param apkFile 欲下载的apk文件
	 * @return
	 */
	public static Intent getInstallIntent(File apkFile)
	{
		Uri uri = Uri.fromFile(apkFile);
		Intent installIntent = new Intent(Intent.ACTION_VIEW);
		installIntent.setDataAndType(uri,
				"application/vnd.android.package-archive");
		return installIntent;
	}

	/**
	 * 发送邮件,可以添加附件。
	 * @param addresses 邮件地址
	 * @param subject 邮件标题
	 * @param content 邮件内容
	 * @param filePath 如不为空,则尝试添加包含该filePath的附件
	 * @return
	 */
	public static Intent getEmailIntent(String[] addresses,String subject,String content,String filePath)
	{
		Intent intent=new Intent(Intent.ACTION_SEND);
		intent.putExtra(Intent.EXTRA_EMAIL, addresses);
		intent.putExtra(Intent.EXTRA_SUBJECT, subject);
		intent.putExtra(Intent.EXTRA_TEXT, content);
		if(filePath!=null){
			intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+filePath));
			intent.setType("image/jpeg");
		}else {
			intent.setType("text/plain");
		}
		return intent;
	}
	
	/**
	 * 从Uri中获取一些数据。必须通过startActivityForResult启动，<br>
	 * 然后在onActivityResult中处理
	 * @param uri
	 * @return
	 */
	public static Intent pickIntent(Uri uri)
	{
		Intent intent=new Intent(Intent.ACTION_PICK, uri);
		return intent;
	}
	
	/**
	 * 获得创建ShortCut的Intent,需要加入权限:<br>
	 * 	<uses-permission android:name="com.android.launcher.permission.INSTALL_SHORTCUT" /><br>
	 * 注意，重复发送广播则会重复创建桌面快捷方式。
	 * @param resource 使用Intent.ShortCutIconResource.FromContext获得
	 * @param shortCutIntent 必须使用分类为Main，并且Action是Launcher的活动作为目标。
	 */
	public static Intent getCreateShortCutIntent(String shortCutName,ShortcutIconResource resource,Intent shortCutIntent)
	{
		final String ACTION_ADD_SHORTCUT = "com.android.launcher.action.INSTALL_SHORTCUT";
		Intent intent=new Intent(ACTION_ADD_SHORTCUT);
//				Intent.ACTION_CREATE_SHORTCUT);
		intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, shortCutName);
		intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, resource);
		//
		intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortCutIntent);
		return intent;
	}
	
	/**
	 * 从浏览器打开指定的Uri
	 * @param uri
	 * @return
	 */
	public static Intent getBrowser(Uri uri)
	{
		Intent intent=new Intent(Intent.ACTION_VIEW,uri);
		return intent;
	}
	
	/**
	 * 使用浏览器打开本地文件
	 * @param f 本地文件
	 * @return
	 */
	public static Intent getLocalBrowser(File f)
	{
		Intent intent=new Intent(Intent.ACTION_WEB_SEARCH,Uri.fromFile(f));
		return intent;
	}
	
	
	/**
	 * 获取android系统的无线网络设置
	 * @return
	 */
	public static Intent getWirelessSettings()
	{
		Intent intent = new Intent("android.settings.WIRELESS_SETTINGS");
		return intent;
	}
	
	/**
	 * quality级别从0开始，越高表示其清晰度越好。0级别是低分辨率情况。<br>
	 * 定义outFile指示拍照后将照片存入该File位置。<br>
	 * <strong>注意:若将outFile设置为null,则可以通过ActivityResult调用data.getExtras().get("data")来获取拍照得到的BitMap图像。<br>
	 * 若outFile不为null。则返回的data为空,请使用outFile访问路径进行操作。</strong>
	 * @param quality
	 * @param outFile
	 * @return
	 */
	public static Intent getCameraPicture(int quality,File outFile)
	{
		Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, quality);
		if(outFile!=null)
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outFile));
		return intent;
	}
}
