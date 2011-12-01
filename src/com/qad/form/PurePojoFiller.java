package com.qad.form;

import java.lang.reflect.Method;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.qad.annotation.FillAble;
import com.qad.annotation.FillType;

/**
 * 仅支持POJO属性与单一属性的映射关系。目前对 {@link FillType}中定义的所有类型都提供了支持。
 * @author 13leaf
 *
 */
public class PurePojoFiller extends POJOFiller{


	public PurePojoFiller(Activity context, String prefix, String postfix) {
		super(context, prefix, postfix);
	}

	public PurePojoFiller(Activity context) {
		super(context);
	}

	public PurePojoFiller(View groupView, String prefix, String postfix) {
		super(groupView, prefix, postfix);
	}

	public PurePojoFiller(View groupView) {
		super(groupView);
	}


	/**
	 * 
	 * @param customFillAble 在FillType为custom时，需要customFillAble的注解来从相关View中找到对应的方法。
	 * @param id 
	 */
	@Override
	protected Object getViewValue(View relateView, FillType type,FillAble customFillAble) {
//		Log.e("13leaf", relateView.getClass()+","+type);
		try{
		switch (type) {
		case text:
			TextView textView=(TextView) relateView;
			return textView.getText().toString();
		case image:
			ImageView imageView=(ImageView) relateView;
			return imageView.getDrawable();
		case progress:
			ProgressBar progressBar=(ProgressBar) relateView;
			return progressBar.getProgress();
		case check:
			CompoundButton checkButton=(CompoundButton) relateView;
			return checkButton.isChecked();
		case hint:
			TextView textView2=(TextView) relateView;
			return textView2.getHint().toString();
		case secondaryProgress:
			ProgressBar progressBar2=(ProgressBar) relateView;
			return progressBar2.getSecondaryProgress();
		case numStar:
			RatingBar ratingBar=(RatingBar) relateView;
			return ratingBar.getNumStars();
		case custom:
			String setMethodName="get"+toWordCaseString(customFillAble.viewField());
			try {
				Method getMethod=relateView.getClass().getMethod(setMethodName, customFillAble.fieldType());
				return getMethod.invoke(relateView);
			} catch(Exception exception){
				exception.printStackTrace();
			}
			break;
		}
		}catch (ClassCastException e) {
			e.printStackTrace();
		}
		return null;
	}

/*	*//**
	 * 设置值到TextView
	 * @param field 这个属性在这里将被忽略。
	 * @param value
	 * @param id
	 *//*
	@Override
	protected void setValue2View(String field, String value, int id) {
		View view=activity.findViewById(id);
		if(view instanceof CompoundButton)
		{
			CompoundButton btn=(CompoundButton) view;
			btn.setChecked(Boolean.parseBoolean(value));
		}else if(view instanceof TextView)
		{
			TextView txt=(TextView)view;
			txt.setText(value);
		}
	}
*/
	
	@Override
	protected void setValue2View(Object fieldValue, View view, FillType type) {
		if(fieldValue==null) return;//忽略设置
		try{
		switch (type) {
		case text:
			String stringValue=fieldValue.toString();
			TextView textView=(TextView) view;
			textView.setText(stringValue);
			break;
		case image:
			Drawable drawable=null;
			Bitmap bitmap=null;
			if(fieldValue instanceof Drawable)
				drawable=(Drawable) fieldValue;
			else {
				bitmap=(Bitmap) fieldValue;
			}
			ImageView imageView=(ImageView) view;
			if(drawable!=null) {
				imageView.setImageDrawable(drawable);
			}
			else {
				imageView.setImageBitmap(bitmap);
			}
			break;
		case check:
			boolean b=Boolean.parseBoolean(fieldValue.toString());
			CompoundButton checkButton=(CompoundButton) view;
			checkButton.setChecked(b);
			break;
		case progress:
			ProgressBar progressBar=(ProgressBar) view;
			progressBar.setProgress(Integer.parseInt(fieldValue.toString()));
			break;
		case secondaryProgress:
			ProgressBar progressBar2=(ProgressBar) view;
			progressBar2.setSecondaryProgress(Integer.parseInt(fieldValue.toString()));
			break;
		case numStar:
			RatingBar ratingBar=(RatingBar) view;
			ratingBar.setNumStars(Integer.parseInt(fieldValue.toString()));//no check long type
			break;
		case custom:
			FillAble customFillAble=fieldValue.getClass().getAnnotation(FillAble.class);
			String setMethodName="set"+toWordCaseString(customFillAble.viewField());
			try {
				Method setMethod=view.getClass().getMethod(setMethodName, customFillAble.fieldType());
				setMethod.invoke(view, fieldValue);
			} catch(Exception exception){
				exception.printStackTrace();
			}
			break;
		}
		}catch (ClassCastException e) {
			e.printStackTrace();
		}
	}
	
}
