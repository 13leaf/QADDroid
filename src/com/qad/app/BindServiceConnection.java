package com.qad.app;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Messenger;
import android.util.Log;

/**
 * 一个简易的ServiceConnection。方便获取服务的绑定状态<br>
 * <strong>注意,用bind启动Service是异步调用。调用成功时会回调ServiceConnection的相关方法</strong><br>
 * 如若想要在连接建立或关闭时做一些自定义操作，只需继承本类并复写相关方法即可。<br>
 * 因此，在通过getService()通信之前务必确定hasBound成功。<br>
 * if(bServiceConn.hasBound){ 
 * mService=bServiceConn.getService()//..
 * }
 * 
 * @author 13leaf
 *@deprecated 不应当封装去除回调
 */
public class BindServiceConnection<T extends IBinder> implements ServiceConnection {
	
	private boolean mBind=false;
	
	private T mService=null;
	
	private Messenger mRemoteService=null;
	
	public BindServiceConnection()
	{
		
	}
	
/*	*//**
	 * 传入IBinder参数将在Service连接建立后有效。
	 * @param mService 与Service交互的IBinder实例
	 *//*
	public BindServiceConnection(T mService)
	{
		this.mService=mService;
	}
	*/
	
	/**
	 * 获取服务的绑定状态
	 * @return
	 */
	public boolean hasBind()
	{
		return mBind;
	}
	
	/**
	 * 当绑定成功后便可获取Service对象。谨记在访问此方法前必须保证服务处于绑定状态
	 * @return 
	 */
	public T getService()
	{
		if(mBind)
		{
			return mService;
		}else {
			throw new RuntimeException("not bounded!");
		}
	}
	
	/**
	 * 返回一个消息包装Service，用于进行远程的IPC通信。
	 * @return
	 */
	public Messenger getRemoteService()
	{
		if(mBind)
		{
			if(mRemoteService==null) 
				mRemoteService=new Messenger(mService);
			return mRemoteService;
		}else{
			throw new RuntimeException("not bounded!");
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		mBind=service!=null;
		if(mBind)
			mService=(T) service;
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		mBind=false;
		mService=null;
		Log.e("13leaf",name.getShortClassName()+" disconnected");
	}

}
