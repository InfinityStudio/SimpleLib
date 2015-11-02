package net.ci010.minecrafthelper.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(value =
{ ElementType.TYPE })
public @interface GenLang
{
	String[] value() default {};
}