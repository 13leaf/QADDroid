package com.qad.demo.render;

import com.qad.render.Render;
import com.qad.render.RenderType;

public class RenderEntitiy2 {
	public RenderEntitiy2(String thumb, String title, String updateTime,
			String content) {
		super();
		this.thumb = thumb;
		this.title = title;
		this.updateTime = updateTime;
		this.content = content;
	}
	@Render(type=RenderType.image)
	private String thumb;
	@Render
	private String title;
	@Render
	private String updateTime;
	@Render
	private String content;
	public String getThumb() {
		return thumb;
	}
	public void setThumb(String thumb) {
		this.thumb = thumb;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
}
