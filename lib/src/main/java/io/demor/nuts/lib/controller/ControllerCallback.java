package io.demor.nuts.lib.controller;

import android.app.Activity;
import android.app.Fragment;
import android.view.View;
import com.google.common.collect.Maps;
import io.demor.nuts.lib.ReflectUtils;

import java.lang.reflect.Field;
import java.util.HashMap;

public abstract class ControllerCallback<T> {

    static final HashMap<Class, Field> CONTEXT_FIELD_MAP = Maps.newHashMap();

    static final Class[] CHECK_CLASSES = {Activity.class, Fragment.class, View.class};

    public ControllerCallback() {
        final Class<?> clz = getClass();
        if (!CONTEXT_FIELD_MAP.containsKey(clz)) {
            for (Field f : getClass().getDeclaredFields()) {
                final Class<?> fieldClz = f.getClass();
                for (Class c : CHECK_CLASSES) {
                    if (ReflectUtils.isSubclassOf(fieldClz, c)) {
                        CONTEXT_FIELD_MAP.put(clz, f);
                        f.setAccessible(true);
                        break;
                    }
                }
                if (CONTEXT_FIELD_MAP.containsKey(clz)) {
                    break;
                }
            }
        }
    }

    public abstract void onResult(T t);

    public void onException(Throwable e) {
    }
}
