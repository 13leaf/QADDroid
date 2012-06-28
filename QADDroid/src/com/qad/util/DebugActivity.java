package com.qad.util;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources.Theme;
import android.os.Bundle;

import com.qad.app.BaseActivity;

/**
 * 打印一些活动生命周期的信息。用于辅助调试<br>
 * 可以设置debugLevel来定义可以看到的生命周期详细程度。
 * @author 13leaf
 *
 */
public class DebugActivity extends BaseActivity {

	/**
	 * <ol>
	 * <li>显示onCreate,onResume,onStop,onDestroy,onNewIntent,OnActivityResult,onApplyTheme</li>
	 * <li> 显示onSavedInstance,onStop,onRestoreInstanceState</li>
	 * <li> 显示onRestart,onStart,onPause,和createDialog,createOptionMenu等</li>
	 * <li>显示onPrepareDialog</li>
	 * </ol>
	 * 当debugLevel>1时，会显示SavedInstance的详细信息
	 */
	public int debugLevel=1;
	
	private String dumpBundle(Bundle bundle)
	{
		if(debugLevel>1)
			return bundle==null?"":bundle.toString();
		else
			return "";
	}
	
	private String dumpIntent(Intent intent)
	{
		if(debugLevel>1)
			return intent==null?"":intent.toString();
		else
			return "";
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		debugLog("create "+dumpBundle(savedInstanceState));
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		if(debugLevel>=2)
			debugLog("restore instance "+dumpBundle(savedInstanceState));
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		if(debugLevel>=3)
			debugLog("start");
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
		debugLog("resume");
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
		if(debugLevel>=2)
			debugLog("restart");
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if(debugLevel>=2)
			debugLog("saveInstance "+dumpBundle(outState));
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if(debugLevel>=3)
			debugLog("pause");
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		if(debugLevel>=2)
			debugLog("stop");
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		debugLog("destroy");
	}
	
	//about dialog
	@Override
	protected Dialog onCreateDialog(int id) {
		if(debugLevel>=3)
			debugLog("create dialog:"+id);
		return super.onCreateDialog(id);
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		super.onPrepareDialog(id, dialog);
		if(debugLevel>=3)
			debugLog("prepare dialog:"+id);
	}
	
	//come in
	@Override
	protected void onNewIntent(Intent intent) {
		debugLog("new intent: "+dumpIntent(intent));
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		debugLog("get Activity Result:"+"(request="+requestCode+",result="+resultCode+") intent="+dumpIntent(data));
	}
	
	@Override
	protected void onApplyThemeResource(Theme theme, int resid, boolean first) {
		super.onApplyThemeResource(theme, resid, first);
		
//		debugLog("change Theme"); //call here will cause null pointer exception.but why??
	}
	
}
