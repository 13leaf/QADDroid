package com.qad.system;

import java.lang.ref.WeakReference;

import android.content.Context;

import com.qad.system.adapter.NetWorkAdapter;
import com.qad.system.adapter.SDCardAdapter;
import com.qad.system.listener.NetworkListioner;
import com.qad.system.listener.SDCardListioner;
import com.qad.system.receiver.NetworkReceiver;
import com.qad.system.receiver.SDCardReceiver;

/**
 * 获取手机的状态信息。为避免内存泄露，不应该为context设置成员变量。<br>
 * 需要如下权限:
 * <ol>
 * <li>&lt;uses-permission android:name="android.permission.ACCESS_WIFI_STATE" /></li>    
   <li>&lt;uses-permission android:name="android.permission.READ_PHONE_STATE" /></li>
 * </ol>
 * 
 * @author 13leaf
 * TODO 增加Executor对Activity进行阻塞式通知。避免因状态不正确导致出现问题
 */
public class PhoneManager{

	private SDCardReceiver sdCardReceiver;
	private SDCardAdapter sdCardAdapter=new SDCardAdapter();
	//
	private NetworkReceiver networkReceiver;
	private NetWorkAdapter netWorkAdapter;
	
	private WeakReference<Context> weakContext;
	
	private PhoneInfo info;
	
	private static PhoneManager instance;
	
	//
	
	public static PhoneManager getInstance(final Context context) {
		if (instance == null) {
			instance = new PhoneManager(context.getApplicationContext());//ensure for application context
		}
		return instance;
	}
	
	public static PhoneManager createInstance(final Context context)
	{
		return new PhoneManager(context);
	}
	
	private PhoneManager(Context context)
	{
		weakContext=new WeakReference<Context>(context);
		sdCardReceiver=new SDCardReceiver(context);
		sdCardReceiver.addOnSDCardListioner(sdCardAdapter);
		
		networkReceiver=new NetworkReceiver(context);
		netWorkAdapter=new NetWorkAdapter(context);
		networkReceiver.addNetworkListioner(netWorkAdapter);
		
		info=new PhoneInfo(context);
	}
	
	@Override
	public boolean equals(Object o) {
		return super.equals(o);
	}
	
	@Override
	public int hashCode() {
		return super.hashCode();
	}
	
	@Override
	public String toString() {
		return super.toString();
	}

	
	public boolean isSDCardAvailiable() {
		return sdCardAdapter.isSDCardAvailiable();
	}

	
	public boolean isConnectedNetwork() {
		return netWorkAdapter.isConnectedNetwork();
	}

	
	public boolean isConnectedWifi() {
		return netWorkAdapter.isConnectedWifi();
	}

	
	public boolean isConnectedMobileNet() {
		return netWorkAdapter.isConnectedMobileNet();
	}
	
	public void addOnSDCardChangeListioner(SDCardListioner listioner) {
		sdCardReceiver.addOnSDCardListioner(listioner);
	}

	
	public void removeSDCardChangeListioner(SDCardListioner listioner) {
		sdCardReceiver.removeOnSDCardListioner(listioner);
	}

	
	public void addOnNetWorkChangeListioner(NetworkListioner listioner) {
		networkReceiver.addNetworkListioner(listioner);
	}

	
	public void removeNetworkChangeListioner(NetworkListioner listioner) {
		networkReceiver.removeNetworkListioner(listioner);
	}
	
	/**
	 * 释放与该Context注册的Receiver资源
	 */
	public void destroy()
	{
		Context context=weakContext.get();
		if(context!=null){
			context.unregisterReceiver(sdCardReceiver);
			context.unregisterReceiver(networkReceiver);
		}
	}

	public String getImei() {
		return info.getImei();
	}

}
