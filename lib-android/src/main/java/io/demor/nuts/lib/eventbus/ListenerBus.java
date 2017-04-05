package io.demor.nuts.lib.eventbus;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.reflect.Reflection;
import io.demor.nuts.lib.NutsApplication;
import io.demor.nuts.lib.annotation.eventbus.DeepClone;
import io.demor.nuts.lib.controller.ControllerUtil;
import io.demor.nuts.lib.server.impl.ApiServer;
import io.demor.nuts.lib.task.SafeTask;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;

public final class ListenerBus {

    private static final Multimap<Class<?>, Object> METHOD_CONSUMER = LinkedListMultimap.create();
    private static final HashMap<Class<?>, ListenerClassContext<?>> METHOD_PROVIDER = Maps.newHashMap();

    private ListenerBus() {
    }

    public static synchronized <T> void register(Class<T> clz, T obj) {
        if (clz == null || obj == null) {
            return;
        }
        addClz(clz);
        METHOD_CONSUMER.put(clz, obj);
    }

    public static synchronized void unregister(Class<?> clz) {
        METHOD_CONSUMER.removeAll(clz);
    }

    public static synchronized void clear() {
        METHOD_CONSUMER.clear();
    }

    public static synchronized <T> T provide(final Class<T> clz) {
        if (METHOD_PROVIDER.containsKey(clz)) {
            return (T) METHOD_PROVIDER.get(clz).mProxy;
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

        ListenerClassContext<T> result = new ListenerClassContext<>(clz);
        METHOD_PROVIDER.put(clz, result);
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
            for (MethodContext context : mMethodList) {
                if (context.mMethod.equals(method)) {
                    for (Object o : METHOD_CONSUMER.get(mClass)) {
                        context.call(o, args);
                    }
                }
            }
            final ApiServer s = NutsApplication.sApiServer;
            if (s != null && s.mCanSendListener) {
                SafeTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        s.sendListenerMethod(ControllerUtil.generateMethodInfo(method, args));
                    }
                });
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
