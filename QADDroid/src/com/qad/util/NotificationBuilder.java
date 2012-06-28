package com.qad.util;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.AndroidRuntimeException;
import android.widget.RemoteViews;

/**
 * 仿照Android3.0 API写出的Builder构造器。有部分属性是2.2新添的，在注释中有说明。
 * <br>因为本版本以1.6系统为准，因此新属性暂不支持。
 * 
 * @author 13leaf
 * 
 */
@SuppressWarnings("unused")
public class NotificationBuilder {
	private Context mContext;
	
	private BuildParams P=new BuildParams();
	
	private final class BuildParams{
		///just for build
		
		////////
		boolean autoCancel;
		RemoteViews customContentViews;
		CharSequence info;
		PendingIntent contentIntent;
		CharSequence contentText;
		CharSequence contentTitle;
		Integer defaults;
		PendingIntent deleteIntent;
		////full screeen
		PendingIntent fullScreenIntent;
		Boolean highPriority;
		
		Bitmap largeIcon;
		Integer number;
		
		//
		boolean ongoing;
		boolean onlyAlertOnce;
		
		Integer smallIcon;
		Integer smallIconLevel;
		
		Uri sound;
		int streamType=Notification.STREAM_DEFAULT;
		
		CharSequence tickerText;
		RemoteViews tickerViews;
		
		long[] vibrate;
		long when=System.currentTimeMillis();
		//flash
		Integer argb;
		Integer onMs;
		Integer offMs;
		
		Notification apply(Notification notification)
		{
			if(contentTitle==null) contentTitle=tickerText;
			if(contentIntent==null) contentIntent=PendingIntent.getActivity(mContext, 0, new Intent(),   // add this pass null to intent
                    PendingIntent.FLAG_UPDATE_CURRENT);
			//FIXED V1 修正了自定义RemoteView时产生的错误。
			if(customContentViews==null && (tickerText==null || contentTitle==null || contentIntent==null))//normal mode,must have all this attribute 
					{
				throw new AndroidRuntimeException("A notification must at least have tickerText,contentTitle,and contentIntent.Or have one customContentView");
			}
			//require set
			//apply icon
			if(smallIcon!=null){
				notification.icon=smallIcon;
				if(smallIconLevel!=null)
					notification.iconLevel=smallIconLevel;
			}else {
				//default icon
				notification.icon=mContext.getApplicationInfo().icon;
			}
			//set to default
			if(customContentViews==null)
			{
				//本方法在3.0以后被废除。可以使用Notification.Builder替代
				notification.setLatestEventInfo(mContext, contentTitle, contentText, contentIntent);
			}else {
				notification.contentView=customContentViews;
				if(contentIntent==null)
				{
					throw new AndroidRuntimeException("A custom notification must at least have cotentIntent");
				}
				notification.contentIntent=contentIntent;
			}
			//set defaults
			if(defaults!=null){
				notification.defaults=defaults;
			}
			
			//optional set
			if(tickerText!=null){
				notification.tickerText=tickerText;
			}
			if(sound!=null){
				notification.sound=sound;
			}
			if(vibrate!=null){
				notification.vibrate=vibrate;
			}
			if(argb!=null){
				notification.ledARGB=argb;
				notification.ledOffMS=offMs;
				notification.ledOnMS=onMs;
				notification.flags|=Notification.FLAG_SHOW_LIGHTS;
			}
			if(number!=null)
			{
				notification.number=number;
			}
			
			//other set
			notification.deleteIntent=deleteIntent;
			notification.when=when;
			notification.audioStreamType=streamType;
			
			if(autoCancel)
				notification.flags|=Notification.FLAG_AUTO_CANCEL;
			if(ongoing)
				notification.flags|=Notification.FLAG_ONGOING_EVENT;
			if(onlyAlertOnce)
				notification.flags|=Notification.FLAG_ONLY_ALERT_ONCE;
			
			return notification;
		}
	}

	public NotificationBuilder(Context context) {
		mContext = context;
	}

	/**
	 * Setting this flag will make it so the notification is automatically canceled when the user clicks it in the panel. 
	 * The PendingIntent set with setDeleteIntent(PendingIntent) will be broadcast when the notification is canceled.
	 * <br>设置该标志后，当用户在面板中点击时通知将自动移除。移除的同时将会广播由setDeleteIntent设置的潜在Intent。
	 * @param autoCancel
	 * @return
	 */
	public NotificationBuilder setAutoCancel(boolean autoCancel) {
		P.autoCancel=autoCancel;
		return this;
	}
	
	/**
	 * 完成build构造后的Notification。
	 * @return
	 */
	public Notification getNotification()
	{
		Notification notification=new Notification();
		return P.apply(notification);
	}

	/**
	 * Supply a custom RemoteViews to use instead of the standard one.
	 * 设置了该属性将认为你使用自定义的内容View。
	 * @param views
	 * @return
	 */
	public NotificationBuilder setContent(RemoteViews views) {
		P.customContentViews=views;
		return this;
	}

	/**
	 * Set the large text at the right-hand side of the notification.
	 * 2.2版才有该支持。
	 * @param info
	 * @return
	 */
	public NotificationBuilder setContentInfo(CharSequence info) {
		P.info=info;
		return this;
	}

	/**
	 * 设置点击该通知后广播的潜在Intent。
	 * @param intent
	 * @return
	 */
	public NotificationBuilder setContentIntent(PendingIntent intent) {
		P.contentIntent=intent;
		return this;
	}

	/**
	 * 设置通知面板中的内容
	 * @param text
	 * @return
	 */
	public NotificationBuilder setContentText(CharSequence text) {
		P.contentText=text;
		return this;
	}

	/**
	 * 设置通知面板中的Title
	 * @param title
	 * @return
	 */
	public NotificationBuilder setContentTitle(CharSequence title) {
		P.contentTitle=title;
		return this;
	}

	/**
	 * Set the default notification options that will be used.
	 * The value should be one or more of the following fields combined with bitwise-or: DEFAULT_SOUND, DEFAULT_VIBRATE, DEFAULT_LIGHTS. 
	 * For all default values, use DEFAULT_ALL.
	 * @param defaults
	 * @return
	 */
	public NotificationBuilder setDefaults(int defaults) {
		P.defaults=defaults;
		return this;
	}

	/**
	 * Supply a PendingIntent to send when the notification is cleared by the user directly from the notification panel. 
	 * For example, this intent is sent when the user clicks the "Clear all" button, or the individual "X" buttons on notifications.
	 * This intent is not sent when the application calls NotificationManager.cancel(int).
	 * <br>
	 * 若使用了清除全部，或者点击"X"手动关闭通知，将会发送该删除Intent。而若设置了AutoCancel，或通过cancel()方法取消，并不会发送DeleteIntent。
	 * @param intent
	 * @return
	 */
	public NotificationBuilder setDeleteIntent(PendingIntent intent) {
		P.deleteIntent=intent;
		return this;
	}

	/**
	 * 2.2版本才有该支持属性。
	 * @param intent
	 * @param highPriority
	 * @return
	 */
	public NotificationBuilder setFullScreenIntent(PendingIntent intent,
			boolean highPriority) {
		P.fullScreenIntent=intent;
		P.highPriority=highPriority;
		return this;
	}

	/**
	 * 2.2版本才有该属性支持.
	 * @param icon
	 * @return
	 */
	public NotificationBuilder setLargeIcon(Bitmap icon) {
		P.largeIcon=icon;
		return this;
	}

	/**
	 * 颜色，亮的毫秒间距，结束的毫秒间距。
	 * @param argb
	 * @param onMs
	 * @param offMs
	 * @return
	 */
	public NotificationBuilder setLights(int argb, int onMs, int offMs) {
		P.argb=argb;
		P.onMs=onMs;
		P.offMs=offMs;
		return this;
	}

	/**
	 * 必须设置为1。否则会不显示
	 * @param number
	 * @return
	 */
	public NotificationBuilder setNumber(int number) {
		P.number=number;
		return this;
	}

	/**
	 * Set whether this is an ongoing notification. <br>
	 * Ongoing notifications differ from regular notifications in the following ways:<br> 
	 * <ol>
	 * <li>Ongoing notifications are sorted above the regular notifications in the notification panel.</li>
	 * <li>Ongoing notifications do not have an 'X' close button, and are not affected by the "Clear all" button.</li>
	 * </ol>
	 * 设置该通知是否是持续不断的。持续不断的通知有如下特征:<br>
	 * 1.处于正在进行的服务面板。在正常通吃面板的上方
	 * 2.不允许出现'X'关闭按钮。并且清除全部对其毫无作用。
	 * @param ongoing
	 * @return
	 */
	public NotificationBuilder setOngoing(boolean ongoing) {
		P.ongoing=ongoing;
		return this;
	}

	/**
	 * Set this flag if you would only like the sound, vibrate and ticker to be played if the notification is not already showing.
	 * 如果你希望Notification只被播放一次动作。如声音、震动、条状通知将只出现一次。
	 * @param onlyAlertOnce
	 * @return
	 */
	public NotificationBuilder setOnlyAlertOnce(boolean onlyAlertOnce) {
		P.onlyAlertOnce=onlyAlertOnce;
		return this;
	}

	/**
	 * 指定显示精度的icon。
	 * @param icon
	 * @param level
	 * @return
	 */
	public NotificationBuilder setSmallIcon(int icon, int level) {
		P.smallIcon=icon;
		P.smallIconLevel=level;
		return this;
	}

	/**
	 * 设置通知面板区显示的小图标。
	 * @param icon 程序资源包下的drawable资源id
	 * @return
	 */
	public NotificationBuilder setSmallIcon(int icon) {
		P.smallIcon=icon;
		return this;
	}

	/**
	 * 若设置了该属性，在发起通知时会播放声音。
	 * @param sound
	 * @return
	 */
	public NotificationBuilder setSound(Uri sound) {
		P.sound=sound;
		return this;
	}

	/**
	 * 使用自定义的流类型设置声音。
	 * @param sound
	 * @param streamType
	 * @return
	 */
	public NotificationBuilder setSound(Uri sound, int streamType) {
		P.sound=sound;
		P.streamType=streamType;
		return this;
	}

	/**
	 * 2.2版本才有TickerView的属性。
	 * @param tickerText
	 * @param views
	 * @return
	 */
	public NotificationBuilder setTicker(CharSequence tickerText,
			RemoteViews views) {
		P.tickerText=tickerText;
		P.tickerViews=views;
		return this;
	}

	/**
	 * 提示的滚动条文本
	 * @param tickerText
	 * @return
	 */
	public NotificationBuilder setTicker(CharSequence tickerText) {
		P.tickerText=tickerText;
		return this;
	}

	/**
	 * 震动设置
	 * @param pattern
	 * @return
	 */
	public NotificationBuilder setVibrate(long[] pattern) {
		P.vibrate=pattern;
		return this;
	}

	/**
	 * 设置通知时间。通知面板依据时间排序通知条
	 * @param when
	 * @return
	 */
	public NotificationBuilder setWhen(long when) {
		P.when=when;
		return this;
	}

}
