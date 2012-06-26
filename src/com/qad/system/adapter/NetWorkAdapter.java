package com.qad.system.adapter;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import com.qad.system.listener.NetworkListioner;

public class NetWorkAdapter implements NetworkListioner {
	
	boolean isConnectedFast;
	
	public boolean isConnectedNetwork() {
		if(activeNetworkInfo != null){
			return activeNetworkInfo.isConnected();
		}else{
			return false;
		}
		
	}
	
    /**
     * Check if the connection is fast
     * @param type
     * @param subType
     * @return
     */
    public boolean isConnectionFast(){
    	//precheck is connected
    	if(!activeNetworkInfo.isConnected()) return false;
    	int type=activeNetworkInfo.getType();
    	int subType=activeNetworkInfo.getSubtype();
        if(type==ConnectivityManager.TYPE_WIFI){
            return true;
        }else if(type==ConnectivityManager.TYPE_MOBILE){
            switch(subType){
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                return false; // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_CDMA:
                return false; // ~ 14-64 kbps
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return false; // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                return true; // ~ 400-1000 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                return true; // ~ 600-1400 kbps
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return false; // ~ 100 kbps
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                return true; // ~ 2-14 Mbps
            case TelephonyManager.NETWORK_TYPE_HSPA:
                return true; // ~ 700-1700 kbps
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                return true; // ~ 1-23 Mbps
            case TelephonyManager.NETWORK_TYPE_UMTS:
                return true; // ~ 400-7000 kbps
           /* // NOT AVAILABLE YET IN API LEVEL 7
            case Connectivity.NETWORK_TYPE_EHRPD:
                return true; // ~ 1-2 Mbps
            case Connectivity.NETWORK_TYPE_EVDO_B:
                return true; // ~ 5 Mbps
            case Connectivity.NETWORK_TYPE_HSPAP:
                return true; // ~ 10-20 Mbps
            case Connectivity.NETWORK_TYPE_IDEN:
                return false; // ~25 kbps 
            case Connectivity.NETWORK_TYPE_LTE:
                return true; // ~ 10+ Mbps
            // Unknown
*/            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                return false; 
            default:
                return false;
            }
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
