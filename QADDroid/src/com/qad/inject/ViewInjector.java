package com.qad.inject;

import java.lang.reflect.Field;

import android.view.View;

import com.qad.annotation.InjectView;
import com.qad.annotation.Nullable;
import com.qad.util.ReflectTool;


/**
 * 通过注解实现的View注入，另请见 {@link InjectView}
 * @author 13leaf
 */
public class ViewInjector {
	
	/**
	 * 迭代指定对象实例，注入所有包含FindView值的字段。
	 * @param decoView
	 * @param instance
	 */
	public static void inject(View decoView,Object instance)
	{
		for(Field annotatedField: ReflectTool.getAnnotedFields(instance.getClass(), InjectView.class))
		{
			inject(decoView, annotatedField.getAnnotation(InjectView.class), 
					instance, annotatedField);
		}
	}
	
	/**
	 * 从指定的Activity中依据对应的字段和注解寻找到指定的View。若寻找结果为null,则会产生异常。如果想允许查找失败,请添加@NullAble注解。
	 * @param decoView
	 * @param annotation
	 * @param instance
	 * @param field
	 */
	public static void inject(View decoView,InjectView annotation,//get
			Object instance,Field field)//set
	{
	        Object value = null;
	        try {
	            value = decoView.findViewById(annotation.id());

	            if (value == null && field.getAnnotation(Nullable.class) == null)
	                throw new NullPointerException(String.format("Can't inject null value into %s.%s when field is not @Nullable", field.getDeclaringClass(), field.getName()));

	            field.setAccessible(true);
	            field.set(instance, value);

	        } catch (IllegalAccessException e) {
	            throw new RuntimeException(e);

	        } catch (IllegalArgumentException f) {
	            throw new IllegalArgumentException(String.format("Can't assign %s value %s to %s field %s", value != null ? value.getClass() : "(null)", value,
	                    field.getType(), field.getName()));
	        }
	}

}
