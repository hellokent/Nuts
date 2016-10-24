package io.demor.nuts.lib.controller;

import io.demor.nuts.lib.ReflectUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.concurrent.Callable;

public class ControllerInvokeHandler<I> implements InvocationHandler {

    protected final I mImpl;
    private final Class<?> mClz;
    private final HashMap<Class, Class> mCastMap;

    public ControllerInvokeHandler(I impl, HashMap<Class, Class> castMap) {
        mCastMap = castMap;
        mImpl = impl;
        mClz = impl.getClass();
        final Class[] interfaces = mClz.getInterfaces();
        for (Class i : interfaces) {
            for (Method method : i.getDeclaredMethods()) {
                final Class[] exceptions = method.getExceptionTypes();
                if (exceptions != null && exceptions.length > 0) {
                    throw new Error("invalid method:" + method.getName() + ", in " + i.getName() + ", " +
                            "because of throwing exceptions");
                }
            }
        }
    }

    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        Class<?> returnClz = method.getReturnType();
        if (ReflectUtils.isSubclassOf(returnClz, Return.class)) {
            if (mCastMap.containsKey(returnClz)) {
                returnClz = mCastMap.get(returnClz);
            }
            return returnClz.getConstructor(Callable.class, Method.class)
                    .newInstance(new ControllerCallable(method, mImpl, args), method);
        } else {
            return new ControllerCallable(method, mImpl, args).call();
        }
    }

}
