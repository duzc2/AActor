package com.ourpalm.hot.aactor;

import java.lang.annotation.Inherited;
import java.lang.annotation.RetentionPolicy;

@java.lang.annotation.Target(value={java.lang.annotation.ElementType.METHOD})
@java.lang.annotation.Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Mailbox {
	public String value() default "";
}
