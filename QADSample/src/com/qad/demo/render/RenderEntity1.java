package com.qad.demo.render;

import android.graphics.Color;

import com.qad.demo.R.id;
import com.qad.render.Render;
import com.qad.render.RenderAll;
import com.qad.render.RenderType;

@RenderAll
public class RenderEntity1 {
	private String account;
	@Render(type=RenderType.none)//不要显示
	private String password;
	private String email;
	private String validateQuestion;
	private String validateAnswear;
	private String validateNumber;
	@Render(type=RenderType.image)
	private String validatePicture;
	private boolean approve;
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	//即使没有对应的getter方法，仍然将通过字段反射获取值
	public String getPassword1() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getValidateQuestion() {
		//优先使用getter
		return validateQuestion==null?"你的母亲姓名是?":validateQuestion;
	}
	public void setValidateQuestion(String validateQuestion) {
		this.validateQuestion = validateQuestion;
	}
	public String getValidateAnswear() {
		return validateAnswear;
	}
	public void setValidateAnswear(String validateAnswear) {
		this.validateAnswear = validateAnswear;
	}
	public String getValidateNumber() {
		return validateNumber;
	}
	public void setValidateNumber(String validateNumber) {
		this.validateNumber = validateNumber;
	}
	public String getValidatePicture() {
		return validatePicture;
	}
	public void setValidatePicture(String validatePicture) {
		this.validatePicture = validatePicture;
	}
	public boolean isApprove() {
		return approve;
	}
	public void setApprove(boolean approve) {
		this.approve = approve;
	}
	
	@Render(id=id.validateNumber,type=RenderType.textColor)
	int getValidNumberColor()
	{
		return Color.RED;
	}
}
