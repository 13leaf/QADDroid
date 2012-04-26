package com.qad.net;

import java.lang.ref.WeakReference;

import com.qad.system.listener.NetworkListioner;

import android.content.Context;
import android.database.Cursor;
import android.net.NetworkInfo;
import android.net.Uri;

/**
 * 调用init初始化。
 * 之后可以通过访问proxy_server和proxy_port来设置代理
 * @author 13leaf
 *
 */
public class ApnManager implements NetworkListioner{
	
	public static interface APNNet {

		String CMWAP = "cmwap";

		String CMNET = "cmnet";

		String GWAP_3 = "3gwap";

		String GNET_3 = "3gnet";

		String UNIWAP = "uniwap";

		String UNINET = "uninet";

		String CTNET = "ctnet";

		String CTWAP = "ctwap";
	}
	
	public final static String CMWAP_SERVER = "10.0.0.172";
	public final static String CTWAP_SERVER = "10.0.0.200";

	static boolean useProxy = false;
	static String proxy_server = CMWAP_SERVER;
	static int proxy_port = 80;
	static String proxy_name=""; 
	
	private static ApnManager instance;
	WeakReference<Context> refContext;
	
	public static ApnManager getInstance(Context context)
	{
		if(instance==null)
			instance=new ApnManager(context);
		return instance;
	}
	
	public ApnManager(final Context context)
	{
		init(context);
		refContext=new WeakReference<Context>(context);
	}
	
	public void init(Context context)
	{
		proxy_name=_init(context);
	}
	
	public static String getProxy_name() {
		return proxy_name;
	}
	
	public static int getProxy_port() {
		return proxy_port;
	}
	
	public static String getProxy_server() {
		return proxy_server;
	}

	private String _init(Context ctx) {
		Uri PREFERRED_APN_URI = Uri
				.parse("content://telephony/carriers/preferapn");
		Cursor cursor = ctx.getContentResolver().query(PREFERRED_APN_URI,
				new String[] { "_id", "apn", "type" }, null, null, null);
		cursor.moveToFirst();
		if (!cursor.isAfterLast()) {
			String apn = cursor.getString(1);
			if ("".equals(apn) || null == apn) {
				useProxy = false;
				return null;
			} else {
				apn = apn.toLowerCase();
				if (apn.startsWith(APNNet.CMNET)) {
					useProxy = false;
					return APNNet.CMNET;
				} else if (apn.startsWith(APNNet.CMWAP)) {
					useProxy = true;
					proxy_server = CMWAP_SERVER;
					return APNNet.CMWAP;
				}

				else if (apn.startsWith(APNNet.GNET_3)) {
					useProxy = false;
					return APNNet.GNET_3;
				} else if (apn.startsWith(APNNet.GWAP_3)) {
					useProxy = true;
					proxy_server = CMWAP_SERVER;
					return APNNet.GWAP_3;
				}

				else if (apn.startsWith(APNNet.UNINET)) {
					useProxy = false;
					return APNNet.UNINET;
				}

				else if (apn.startsWith(APNNet.UNIWAP)) {
					useProxy = true;
					proxy_server = CMWAP_SERVER;
					return APNNet.UNIWAP;
				}

				else if (apn.startsWith(APNNet.CTNET)) {
					useProxy = false;
					return APNNet.CTNET;
				} else if (apn.startsWith(APNNet.CTWAP)) {
					useProxy = true;
					proxy_server = CTWAP_SERVER;
					return APNNet.CTWAP;
				} else {
					useProxy = false;
					return "";
				}
			}
		} else {
			useProxy = false;
			return null;
		}
	}

	@Override
	public void onWifiConnected(NetworkInfo networkInfo) {
		useProxy=false;
	}

	@Override
	public void onMobileConnected(NetworkInfo networkInfo) {
		Context context=refContext.get();
		if(context!=null)
			init(context);
	}

	@Override
	public void onDisconnected(NetworkInfo networkInfo) {
		
	}
}
