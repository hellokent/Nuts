package com.nuts.lib.jumper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Intent的参数
 * Created by chenyang.coder@gmail.com on 14-3-2 下午2:53.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.PARAMETER})
public @interface IntentFlag {
    int[] value() default {};
}
