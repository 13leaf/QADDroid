package com.qad.render;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 被设置RenderAll的类型，将默认所有的字段都是RenderAble。<br>
 * 若注解Class为RenderAll后，对其字段也设置了Render。那么将以后者为准<br>
 * 黑名单可以通过RenderType的none来完成。
 * @author 13leaf
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RenderAll {

}
