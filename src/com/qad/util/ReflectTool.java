package com.qad.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;


public class ReflectTool {
	
	/**
	 * 查找对应的类型中所有被标注过的字段。本方法会进行递归查询，父类中的字段也会进行迭代。
	 * @param clazz
	 * @param annotated
	 * @return
	 */
	public static List<Field> getAnnotedFields(Class<?> clazz,Class<? extends Annotation> annotated)
	{
		List<Field> fields=new LinkedList<Field>();
		for(Class<?> currentClass=clazz;currentClass!=Object.class;currentClass=currentClass.getSuperclass())
		{
			for(Field field : currentClass.getDeclaredFields())
			{
				if(field.getAnnotation(annotated)!=null){
					fields.add(field);
				}
			}
		}
		return fields;
	}
	
}
