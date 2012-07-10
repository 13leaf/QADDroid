package com.qad.system.adapter;

import android.content.Intent;
import android.os.BatteryManager;

import com.qad.system.listener.BatteryListener;

public class BatteryAdapter implements BatteryListener{

	int health;//电池健康情况
	int level;//电池级别
	int plugged;//插入情况
	boolean present;//?
	int max;//电量最大值
	int status;//状态
	String technology;//电池技术描述
	int temperature;//温度,0.1度单位
	int voltage;//电压值
	boolean low=false;//是否处于电量不足状态
	
	@Override
	public void onBatteryChange(int level, int max,Intent intent) {
		this.level=level;
		this.max=max;
		this.health=intent.getIntExtra(BatteryManager.EXTRA_HEALTH, 0);
		this.plugged=intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0);
		this.present=intent.getBooleanExtra(BatteryManager.EXTRA_PRESENT, false);
		this.status=intent.getIntExtra(BatteryManager.EXTRA_STATUS, 0);
		this.technology=intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY);
		this.temperature=intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0);
		this.voltage=intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);
	}
	
	@Override
	public void onBatteryLow() {
		low=true;
	}

	@Override
	public void onBatteryOKAY() {
		low=false;
	}
	
	public int getBatteryStatus()
	{
		return status;
	}
	
	/**
	 * 是否处于电量不足的状态
	 * @return
	 */
	public boolean isBatteryLow() {
		return low;
	}

	public int getBatteryHealth() {
		return health;
	}

	public int getBatteryLevel() {
		return level;
	}

	public int getBatteryPlugged() {
		return plugged;
	}

	public boolean isBatteryPresent() {
		return present;
	}

	public int getBatteryMax() {
		return max;
	}

	public String getBatteryTechnology() {
		return technology;
	}

	public int getBatteryTemperature() {
		return temperature;
	}

	public int getBatteryVoltage() {
		return voltage;
	}

}
