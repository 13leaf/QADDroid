package com.qad.system.receiver;

import java.util.LinkedList;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.qad.system.listener.NetworkListioner;

/**
 * FIXME Issue:当程序首次启动的时候,都将触发一次网络环境改变
 * @author 13leaf
 *
 */
public class NetworkReceiver extends AbstractReceiver {
	
	private final LinkedList<NetworkListioner> listioners=new LinkedList<NetworkListioner>();
	private final Object lock=new Object();
	
	public NetworkReceiver(Context context)
	{
		super(context);
	}

	/**
	 * FIXME 这里的注册也许会有缺陷，详情请见:
	 * Issue:经过p1000测试,在wifi断开网络连接的时候的确会有问题
	 * http://stackoverflow.com/questions/5276032/connectivity-action-intent-recieved-twice-when-wifi-connected?answertab=active#tab-top
	 */
	@Override
	protected IntentFilter getIntentFilter() {
		IntentFilter intentFilter=new IntentFilter();
		intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		return intentFilter;
	}
	
	public void addNetworkListioner(NetworkListioner listioner)
	{
		synchronized (lock) {
			if(!listioners.contains(listioner))
				listioners.add(listioner);
		}
	}
	
	public void removeNetworkListioner(NetworkListioner listioner)
	{
		synchronized (lock) {
			listioners.remove(listioner);
		}
	}


	@Override
	public void onReceive(Context context, Intent intent) {
		NetworkInfo affectedNetworkInfo=
				intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
		boolean disconnect=!affectedNetworkInfo.isConnected();
		synchronized (lock) {
			if(disconnect){
				for(NetworkListioner listioner:listioners)
					listioner.onDisconnected(affectedNetworkInfo);
			}else{
				if(affectedNetworkInfo.getType()==ConnectivityManager.TYPE_WIFI){
					for(NetworkListioner listioner:listioners)
						listioner.onWifiConnected(affectedNetworkInfo);
				}else if(affectedNetworkInfo.getType()==ConnectivityManager.TYPE_MOBILE){
					for(NetworkListioner listioner:listioners)
						listioner.onMobileConnected(affectedNetworkInfo);
				}
			}
		}
	}
}
