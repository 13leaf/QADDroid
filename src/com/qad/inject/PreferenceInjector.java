package com.qad.inject;

import java.lang.reflect.Field;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.qad.annotation.InjectPreference;
import com.qad.annotation.Nullable;
import com.qad.util.ReflectTool;

/**
 * 获取Preference的注入值。注入失败的话将使用如下默认值:
 * <ol>
 *  <li>String->null</li>
 *  <li>boolean->false</li>
 *  <li>数字类型->0</li>
 * </ol>
 * 如想自行设置默认值,建议在xml中设置defaultValue并在BaseApplication的OnCreate中调用ensureDefaultPreference()
 * @author 13leaf
 * 
 */
public class PreferenceInjector {

	public static void inject(Context context, Object instance) {
		for (Field annotatedField : ReflectTool.getAnnotedFields(
				instance.getClass(), InjectPreference.class)) {
			inject(context,
					annotatedField.getAnnotation(InjectPreference.class),
					instance, annotatedField);
		}
	}

	/**
	 * 注入Preference内容
	 * 
	 * @param context
	 * @param annotation
	 * @param instance
	 * @param field
	 */
	public static void inject(Context context, InjectPreference annotation,
			Object instance, Field field) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		String key = annotation.name().length()==0?null:annotation.name();
		if (key==null) {
			// try to load by resource
			key = context.getApplicationContext().getApplicationContext()
					.getString(annotation.id());
		}
		if (key== null && field.getAnnotation(Nullable.class) == null){
            throw new NullPointerException("Please ensure set key name or has valid key id!");
		}
		Object value = null;
		final Class<?> t = field.getType();
		if (String.class.isAssignableFrom(t)) {
			value = preferences.getString(key, null);
		} else if (boolean.class.isAssignableFrom(t)
				|| Boolean.class.isAssignableFrom(t)) {
			value = preferences.getBoolean(key, false);
		} else if (int.class.isAssignableFrom(t)
				|| Integer.class.isAssignableFrom(t)) {
			value = preferences.getInt(key, 0);
		} else if (long.class.isAssignableFrom(t)
				|| Long.class.isAssignableFrom(t)) {
			value = preferences.getFloat(key, 0);
		} else if (float.class.isAssignableFrom(t)
				|| Float.class.isAssignableFrom(t)) {
			value = preferences.getFloat(key, 0);
		}

		field.setAccessible(true);
		try {
			field.set(instance, value);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

	}
}
