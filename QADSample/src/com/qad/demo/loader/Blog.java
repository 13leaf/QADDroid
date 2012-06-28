package com.qad.demo.loader;

import java.io.Serializable;

public class Blog implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8297023625648627610L;
	private String title;
	private String thumbnail="";
	private String description;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getThumbnail() {
		return thumbnail;
	}
	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}
