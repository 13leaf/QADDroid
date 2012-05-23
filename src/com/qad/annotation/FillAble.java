package com.qad.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.qad.render.Render;


/**
 * 
 * FillAble用于字段描述所属类型的信息。被标注为FillAble的字段会在使用PojoFiller的时候被填充，否则将被忽略。<br>
 * 默认情况下FillType是处于自动填充状态的，它会根据被标注的类型进行直接映射。<strong>默认标注类型如下:</strong><br>
 * <ul>
 * <li>String/double/float->text</li>
 * <li>Bitmap/Drawable->image</li>
 * <li>long/int/short/byte->progress</li>
 * <li>boolean->check</li>
 * <li>亦可设置FillType为custome后应当自定义其viewField属性和fieldType属性。</li>
 * </ul>
 * @author 13leaf
 *@deprecated 使用{@link Render}替代
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FillAble {

	/**
	 * 定义对应字段的填充类型。若不设置，则默认为根据字段类型自动填充
	 * @return
	 */
	FillType type() default FillType.auto;
	String viewField() default "";
	Class<?> fieldType() default Object.class;
	
}
