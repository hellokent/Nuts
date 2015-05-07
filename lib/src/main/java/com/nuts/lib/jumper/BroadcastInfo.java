package com.nuts.lib.jumper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 描述广播的发送
 * Created by demor on 14-3-7.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BroadcastInfo {
    String value();

    boolean local() default true;
}
