package com.qad.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注解可以填充的Pojo类型。设置其all属性将会对所有的字段进行填充。<br>
 * 默认情况下all为打开状态
 * @author 13leaf
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface FillPojo {
	boolean all() default true;
}
