package com.qad.demo.loader;

import java.io.File;
import java.text.ParseException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.widget.TextView;

import com.qad.app.BaseActivity;
import com.qad.demo.R.id;
import com.qad.demo.R.layout;
import com.qad.loader.BeanLoader;
import com.qad.loader.LoadContext;
import com.qad.loader.LoadListener;
import com.qad.loader.service.LoadServices;
import com.qad.loader.service.ParseAble;

public class Loader4 extends BaseActivity implements LoadListener{

	private BeanLoader<Activity> loader=new BeanLoader<Activity>();
	
	private String weatherUrl="http://m.weather.com.cn/data/101200101.html";
	
	File cacheFolder = new File(Environment.getExternalStorageDirectory(),
			"qad/cache");
	
	static class WeatherParser implements ParseAble<WeatherBean>
	{

		@Override
		public WeatherBean parse(String s) throws ParseException {
			try {
				JSONObject object=new JSONObject(s);
				JSONObject weather=object.getJSONObject("weatherinfo");
				WeatherBean bean=new WeatherBean();
				bean.setCity(weather.getString("city"));
				bean.setDate(weather.getString("date_y"));
				bean.setTemperature(weather.getString("temp1"));
				return bean;
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			return null;
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		loader.addLoadService(LoadServices.newHttp2Cache(new WeatherParser(), cacheFolder, LoadServices.FLAG_LOAD_FIRST), WeatherBean.class);
		loader.addListener(this, WeatherBean.class);
		setContentView(layout.loader4);
		loader.startLoading(new LoadContext<String, Activity, Object>(weatherUrl, this),WeatherBean.class);
		getDefaultProgressDialog().show();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		loader.removeListener(this,WeatherBean.class);
		loader.destroy(false);
	}

	@Override
	public void loadComplete(LoadContext<?, ?, ?> context) {
		showMessage("加载成功");
		WeatherBean bean=(WeatherBean) context.getResult();
		render(bean);
		getDefaultProgressDialog().hide();
	}

	private void render(WeatherBean bean) {
		TextView city=(TextView) findViewById(id.city);
		city.setText(bean.getCity());
		TextView date=(TextView) findViewById(id.date);
		date.setText(bean.getDate());
		TextView temperature=(TextView) findViewById(id.temperature);
		temperature.setText(bean.getTemperature());
	}

	@Override
	public void loadFail(LoadContext<?, ?, ?> context) {
		showMessage("加载失败");
		getDefaultProgressDialog().hide();
	}
}
