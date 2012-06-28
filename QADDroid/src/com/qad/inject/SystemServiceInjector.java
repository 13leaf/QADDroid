package com.qad.inject;

import java.lang.reflect.Field;

import android.content.Context;

import com.qad.annotation.InjectSystemService;
import com.qad.annotation.Nullable;
import com.qad.util.ReflectTool;

/**
 * 通过注解注入一个系统服务。另请见 {@link InjectSystemService}
 * @author 13leaf
 *
 */
public class SystemServiceInjector {
	/**
	 * 迭代对象实例，注入所有包含FindSystemService的字段。
	 * @param context
	 * @param instance
	 */
	public static void inject(Context context,Object instance)
	{
		for(Field annotatedField : ReflectTool.getAnnotedFields(instance.getClass(), InjectSystemService.class))
		{
			inject(context, annotatedField.getAnnotation(InjectSystemService.class),
					instance, annotatedField);
		}
	}

	/**
	 * 通过context和注解将值注入进入对应实例的字段中去。若注入结果为null，则将抛出异常。如果要允许注入结果失败，请添加@Nullable的注解。
	 * @param context
	 * @param annotation
	 * @param instance
	 * @param field
	 */
	public static void inject(Context context, InjectSystemService annotation,//get
			Object instance, Field field) {//set
		Object value = null;
		//
		final String servieName = annotation.name();
		value = context.getSystemService(servieName);

		if (value == null && field.getAnnotation(Nullable.class) == null) {
			throw new NullPointerException(
					String.format(
							"Can't inject null value into %s.%s when field is not @Nullable",
							field.getDeclaringClass(), field.getName()));
		}

		field.setAccessible(true);
		try {
			field.set(instance, value);
		}
		catch (IllegalAccessException e) {
			throw new RuntimeException(e);

		}
		catch (IllegalArgumentException f) {
			throw new IllegalArgumentException(String.format(
					"Can't assign %s value %s to %s field %s",
					value != null ? value.getClass() : "(null)", value, field
							.getType(), field.getName()));
		}
	}
}
