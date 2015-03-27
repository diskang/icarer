package com.sjtu.icarer.core.login;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

import javax.inject.Qualifier;

@Qualifier
@Retention(RUNTIME)
public @interface UserType {
    String value() default "";//username
}
