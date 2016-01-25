package io.demor.nuts.lib.eventbus;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.reflect.Reflection;
import io.demor.nuts.lib.annotation.eventbus.DeepClone;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;

public final class ListenerBus {

    private static final Multimap<Class<?>, Object> mMethodConsumer = ArrayListMultimap.create();
    private static final HashMap<Class<?>, ListenerClassContext<?>> mMethodProvider = Maps.newHashMap();

    public static synchronized <T> void register(Class<T> clz, T obj) {
        if (clz == null || obj == null) {
            return;
        }
        addClz(clz);
        mMethodConsumer.put(clz, obj);
    }

    public static synchronized void unregister(Class<?> clz) {
        mMethodConsumer.removeAll(clz);
    }

    public static synchronized void clean() {
        mMethodConsumer.clear();
    }

    public static synchronized <T> T provide(final Class<T> clz) {
        if (mMethodProvider.containsKey(clz)) {
            return (T) mMethodProvider.get(clz).mProxy;
        } else {
            return addClz(clz).mProxy;
        }
    }

    private static <T> ListenerClassContext<T> addClz(Class<T> clz) {
        if (!clz.isInterface()) {
            throw new IllegalArgumentException("clz must be interface");
        }

        if (clz.getSuperclass() != null) {
            throw new IllegalArgumentException("clz cannot have super interface");
        }

        ListenerClassContext<T> result = new ListenerClassContext<T>(clz);
        mMethodProvider.put(clz, result);
        return result;
    }

    private static class ListenerClassContext<T> extends ClassContext implements InvocationHandler {

        T mProxy;
        boolean mNeedDeepClone = false;

        ListenerClassContext(Class<T> clz) {
            super(clz);
            mProxy = Reflection.newProxy(clz, this);
            if (clz.getAnnotation(DeepClone.class) != null) {
                mNeedDeepClone = true;
            }
        }

        @Override
        public Object invoke(Object proxy, final Method method, final Object[] args) throws Throwable {
            //TODO map?
            for (MethodContext context : mMethodList) {
                if (context.mMethod.equals(method)) {
                    for (Object o : mMethodConsumer.get(mClass)) {
                        context.call(o, args);
                    }
                }
            }
            return null;
        }

        @Override
        protected ThreadType onNullEventAnnotation(Method method) {
            return ThreadType.MAIN;
        }

        @Override
        protected MethodContext createMethodContext(Method method, ThreadType threadType) {
            return new ListenerMethodContext(method, threadType, mNeedDeepClone);
        }
    }

    private static class ListenerMethodContext extends MethodContext {

        boolean mNeedDeepClone;

        ListenerMethodContext(Method method, ThreadType threadType, boolean needDeepClone) {
            super(method, threadType);
            mNeedDeepClone = needDeepClone;
            if (method.getAnnotation(DeepClone.class) != null) {
                mNeedDeepClone = true;
            }
        }

        @Override
        protected boolean needDeepClone() {
            return mNeedDeepClone;
        }
    }
}
