package com.qad.inject;

import java.lang.reflect.Field;

import android.os.Bundle;

import com.qad.annotation.InjectExtras;
import com.qad.util.ReflectTool;


/**
 * 通过注解实现的Extra内容注入。另请见 {@link InjectExtras}
 * TODO 增加自动转型cast
 * @author 13leaf
 *
 */
public class ExtrasInjector {
	
	/**
	 * 从指定的对象实例中查找所有包含FindExtras标注的字段，并进行注入。
	 * @param extras
	 * @param instance
	 */
	public static void inject(Bundle extras,Object instance)
	{
		for(Field annotatedField: ReflectTool.getAnnotedFields(instance.getClass(), InjectExtras.class))
		{
			inject(extras,annotatedField.getAnnotation(InjectExtras.class), 
					instance, annotatedField);
		}
	}
	
	/**
	 * 将指定注解的字段值注入为该extras的值。若注入结果为null，则将产生异常。若要允许null行为，则需要添加@Nullable的注解。
	 * @param extras
	 * @param annotation
	 * @param instance
	 * @param field
	 */
	public static void inject(Bundle extras,InjectExtras annotation,//get
			Object instance,Field field)//set
	{
        Object value;
        
        final String id = annotation.name();

        if (extras == null || !extras.containsKey(id)) {
            // If no extra found and the extra injection is optional, no
            // injection happens.
            if (annotation.optional()) {
                return;
            } else {
                throw new IllegalStateException(String.format("Can't find the mandatory extra identified by key [%s] on field %s.%s", id, field
                        .getDeclaringClass(), field.getName()));
            }
        }

        value = extras.get(id);

        field.setAccessible(true);
        try {

            field.set(instance, value);

        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);

        } catch (IllegalArgumentException f) {
            throw new IllegalArgumentException(String.format("Can't assign %s value %s to %s field %s", value != null ? value.getClass() : "(null)", value,
                    field.getType(), field.getName()));
        }
	}
}
