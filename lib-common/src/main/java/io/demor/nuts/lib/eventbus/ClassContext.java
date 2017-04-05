package io.demor.nuts.lib.eventbus;

import io.demor.nuts.lib.annotation.eventbus.Event;

import java.lang.reflect.Method;
import java.util.ArrayList;

abstract class ClassContext {
    ArrayList<MethodContext> mMethodList = new ArrayList<>();

    Class<?> mClass;

    ClassContext(Class<?> clz) {
        mClass = clz;
        while (clz != null) {
            for (Method method : clz.getDeclaredMethods()) {
                final Event event = method.getAnnotation(Event.class);
                final ThreadType threadType;
                if (event == null) {
                    threadType = onNullEventAnnotation(method);
                } else {
                    threadType = event.runOn();
                }
                if (threadType == null) {
                    continue;
                }
                final MethodContext context = createMethodContext(method, threadType);
                if (context != null) {
                    mMethodList.add(createMethodContext(method, threadType));
                }
            }
            clz = clz.getSuperclass();
        }
    }

    abstract MethodContext createMethodContext(final Method method, final ThreadType threadType);

    protected ThreadType onNullEventAnnotation(final Method method) {
        return null;
    }

}
