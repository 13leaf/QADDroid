package com.qad.inject;

import java.lang.reflect.Field;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Movie;
import android.graphics.drawable.Drawable;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.qad.annotation.InjectResource;
import com.qad.annotation.Nullable;
import com.qad.util.ReflectTool;

/**
 * 通过注解实现的资源内容注入。另见 {@link InjectResource}
 * @author 13leaf
 *
 */
public class ResourceInjector {
	
	/**
	 * 通过反射将所有对应注解的字段进行查询获取。标注字段按照递归进行查询
	 * @param applicationContext 上下文环境
	 * @param instance 反射目标对象实例
	 */
	public static void inject(Context applicationContext,Object instance)
	{
		for(Field annotatedField : ReflectTool.getAnnotedFields(instance.getClass(), InjectResource.class))
		{
			inject(applicationContext, annotatedField.getAnnotation(InjectResource.class), 
					instance, annotatedField);
		}
	}
	
	/**
	 * 从applicationContext中依据字段和字段对应的寻找资源的注解来寻找并设置指定的字段，并将其设置到instance中。若没有找到对应的字段,将产生异常。
	 * 你可以通过注解的optional属性为true来允许该注入是可选的。(允许null)
	 */
	public static void inject(Context applicationContext,InjectResource annotation,//get
			Object instance,Field field)//set
	{
        Object value = null;

        try {
            final int id = annotation.id();
            final Class<?> t = field.getType();
            final Resources resources = applicationContext.getResources();

            if (String.class.isAssignableFrom(t)) {
                value = resources.getString(id);
            } else if (boolean.class.isAssignableFrom(t) || Boolean.class.isAssignableFrom(t)) {
                value = resources.getBoolean(id);
            } else if (ColorStateList.class.isAssignableFrom(t)  ) {
                value = resources.getColorStateList(id);
            } else if (int.class.isAssignableFrom(t) || Integer.class.isAssignableFrom(t)) {
                value = resources.getInteger(id);
            } else if (Drawable.class.isAssignableFrom(t)) {
                value = resources.getDrawable(id);
            } else if (String[].class.isAssignableFrom(t)) {
                value = resources.getStringArray(id);
            } else if (int[].class.isAssignableFrom(t) || Integer[].class.isAssignableFrom(t)) {
                value = resources.getIntArray(id);
            } else if (Animation.class.isAssignableFrom(t)) {
                value = AnimationUtils.loadAnimation(applicationContext, id);
            } else if (Movie.class.isAssignableFrom(t)  ) {
                value = resources.getMovie(id);
            }
            
            if (value == null && field.getAnnotation(Nullable.class) == null) {
                throw new NullPointerException(String.format("Can't inject null value into %s.%s when field is not @Nullable", field.getDeclaringClass(), field
                        .getName()));
            }

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
