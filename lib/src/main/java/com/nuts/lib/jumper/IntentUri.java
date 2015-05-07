package com.nuts.lib.jumper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Intent的Uri参数 (String - Uri.parse, Uri, File - Uri.fromFile())
 * Created by chenyang.coder@gmail.com on 14-3-2 下午11:22.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface IntentUri {
}
