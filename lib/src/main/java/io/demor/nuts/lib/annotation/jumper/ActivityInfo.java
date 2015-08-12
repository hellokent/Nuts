package io.demor.nuts.lib.annotation.jumper;

import android.app.Activity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 描述Activity信息
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ActivityInfo {

    Class<? extends Activity> clz() default Activity.class;

    String action() default "";

    int[] defaultFlags() default {};
}
