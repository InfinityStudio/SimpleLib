package net.infstudio.infinitylib.api.registry;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author ci010
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ModConfig
{
	String categoryId() default "default";

	String id();

	String defualtValue();

//	Property.Type type() default Property.Type.PROP_STRING;

	boolean showInGui() default false;

	boolean requireRestart() default false;

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	@interface ValidRange
	{
		/**
		 * @return The valid range of the config. If the config is number, it will find the max/min value in this
		 * range to set as maximum value and minimum value.
		 */
		String[] range();
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	@interface List
	{
		int maxLength();
	}
}
