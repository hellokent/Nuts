package com.nuts.lib.jumper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Intent的Key
 * Created by chenyang.coder@gmail.com on 14-3-2 下午2:43.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Extra {
    String value();

    boolean option() default false;
}
