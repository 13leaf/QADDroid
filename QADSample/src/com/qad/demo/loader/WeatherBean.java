package com.qad.demo.loader;

import java.io.Serializable;

public class WeatherBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8789282947598072483L;
	private String city;
	private String date;
	private String temperature;
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getTemperature() {
		return temperature;
	}
	public void setTemperature(String temperature) {
		this.temperature = temperature;
	}
}
