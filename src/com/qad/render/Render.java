package com.qad.render;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 为字段或者方法设置渲染属性。若是方法的话，则必须是个空参数，有返回类型的返回。
 * <ol>
 * <li>namespace用于设置命名空间，当一个实体需要服务多个layout时来做区分。</li>
 * <li>id表示目标view的id。若不设置，则将通过反射查找id与字段同名</li>
 * <li>type标志渲染类别。auto情况将根据字段类型自动设置类别。当设置为custom时,将启用setter完成自定义注入。</li>
 * <li>方法名称。用于反射设置view.setXXX(fieldData)</li>
 * </ol>
 * @author 13leaf
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(value={ElementType.FIELD,ElementType.METHOD})
public @interface Render {
	int id() default 0;
	RenderType type() default RenderType.auto;
	String setter() default "";
}
