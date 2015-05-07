package com.nuts.lib.jumper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Intent的Type
 * Created by chenyang.coder@gmail.com on 14-3-2 下午11:25.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.METHOD})
public @interface IntentType {
    String value() default "";
}
