package com.tann.dice.test.util;

import com.tann.dice.platform.control.Control;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface TestPlat {
   Class<? extends Control> platformClass();
}
