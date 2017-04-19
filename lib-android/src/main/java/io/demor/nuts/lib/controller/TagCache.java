package io.demor.nuts.lib.controller;

import android.os.Handler;
import android.os.HandlerThread;
import com.google.common.base.Strings;
import com.google.common.util.concurrent.SettableFuture;
import io.demor.nuts.lib.annotation.controller.ThreadTag;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;


final class TagCache {

    static final String NO_TAG = "no_tag";

    private static final HashMap<Method, String> CACHE_MAP = new HashMap<>();
    private static final HashMap<String, Handler> HANDLER_MAP = new HashMap<>();

    static synchronized String getTag(Method m) {
        if (CACHE_MAP.containsKey(m)) {
            return CACHE_MAP.get(m);
        }
        ThreadTag tagAnnotation = m.getAnnotation(ThreadTag.class);
        if (tagAnnotation == null) {
            tagAnnotation = m.getClass().getAnnotation(ThreadTag.class);
        }

        if (tagAnnotation == null) {
            for (Class<?> c : m.getClass().getInterfaces()) {
                tagAnnotation = c.getAnnotation(ThreadTag.class);
                if (tagAnnotation != null) {
                    break;
                }
            }
        }

        if (tagAnnotation == null) {
            CACHE_MAP.put(m, NO_TAG);
            return NO_TAG;
        } else {
            CACHE_MAP.put(m, tagAnnotation.value());
            return tagAnnotation.value();
        }
    }

    static synchronized <T> Future<T> run(final String tag, final Callable<T> callable) {
        final SettableFuture<T> result = SettableFuture.create();

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    result.set(callable.call());
                } catch (Exception e) {
                    result.setException(e);
                }
            }
        };

        if (NO_TAG.equals(tag) || Strings.isNullOrEmpty(tag)) {
            throw new Error("invalid tag:" + tag);
        }

        if (!HANDLER_MAP.containsKey(tag)) {
            HANDLER_MAP.put(tag, new Handler(new HandlerThread(tag + "-thread") {{
                start();
            }}.getLooper()));
        }
        HANDLER_MAP.get(tag).post(runnable);

        return result;
    }
}
