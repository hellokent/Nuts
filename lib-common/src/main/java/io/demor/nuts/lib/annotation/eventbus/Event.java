package io.demor.nuts.lib.annotation.eventbus;

import io.demor.nuts.lib.eventbus.ThreadType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Event {
    ThreadType runOn();
}
