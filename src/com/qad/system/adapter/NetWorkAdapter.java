package com.qad.system.adapter;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.qad.system.listener.NetworkListioner;

public class NetWorkAdapter implements NetworkListioner {
	
	
	public boolean isConnectedNetwork() {
		if(activeNetworkInfo != null){
			return activeNetworkInfo.isConnected();
		}else{
			return false;
		}
		
	}

	public boolean isConnectedWifi() {
		return isConnectedNetwork()&& activeNetworkInfo.getType()==ConnectivityManager.TYPE_WIFI;
	}

	public boolean isConnectedMobileNet() {
		return isConnectedNetwork() && activeNetworkInfo.getType()==ConnectivityManager.TYPE_MOBILE;
	}
	
	private NetworkInfo activeNetworkInfo;
	
	public NetWorkAdapter(final Context context)
	{
		ConnectivityManager manager=
				(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		activeNetworkInfo=manager.getActiveNetworkInfo();
	}

	@Override
	public void onWifiConnected(NetworkInfo networkInfo) {
		activeNetworkInfo=networkInfo;
	}

	@Override
	public void onMobileConnected(NetworkInfo networkInfo) {
		activeNetworkInfo=networkInfo;
	}

	@Override
	public void onDisconnected(NetworkInfo networkInfo) {
		activeNetworkInfo=networkInfo;
	}

}
