package com.qad.system.listener;

import android.content.Intent;

public interface BatteryListener {

	/**
	 * 当电池电量发生更改时
	 * @param level 电池电量级别
	 * @param max 电池最高电量
	 * @param intent 其余intent参数
	 */
	void onBatteryChange(int level,int max,Intent intent);
	
	/**
	 * 当系统发现电量不足时广播
	 */
	void onBatteryLow();
	
	/**
	 * 在low之后触发,标明电池恢复正常
	 */
	void onBatteryOKAY();
}
