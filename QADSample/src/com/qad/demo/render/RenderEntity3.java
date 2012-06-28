package com.qad.demo.render;

import com.qad.render.Render;

public class RenderEntity3 {

	@Render
	private String title;

	public RenderEntity3(String title) {
		super();
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
