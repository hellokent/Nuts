package com.nuts.lib.jumper;

import android.app.Activity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 描述Activity信息
 * Created by chenyang.coder@gmail.com on 14-3-2 下午2:45.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ActivityInfo {

    Class<? extends Activity> clz() default Activity.class;

    String action() default "";

    int[] defaultFlags() default {};
}
