package com.sjtu.icarer.core.utils;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;

import javax.inject.Qualifier;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
@Qualifier
@Documented
@Retention(RUNTIME)
public @interface Named {
  String value() default "";
}