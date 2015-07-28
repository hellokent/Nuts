package com.nuts.lib.controller;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.Callable;

import com.nuts.lib.Globals;
import com.nuts.lib.ReflectUtils;

public class ProxyInvokeHandler<I> implements InvocationHandler {

    final I mInterface;
    final Class<?> mClz;

    public ProxyInvokeHandler(I interfaceInstance) {
        mInterface = interfaceInstance;
        mClz = mInterface.getClass();
    }

    public I createProxy() {
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
        Globals.BUS.register(mInterface);
        return (I) Proxy.newProxyInstance(ProxyInvokeHandler.class.getClassLoader(), mClz.getInterfaces(), this);
    }

    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        final Class<?> returnClz = method.getReturnType();
        if (ReflectUtils.isSubclassOf(returnClz, Return.class)) {
            return returnClz.getConstructor(Callable.class, Method.class)
                    .newInstance(new ControllerCallable(method, mInterface, args), method);
        } else {
            return new ControllerCallable(method, mInterface, args).call();
        }
    }
}
