package io.demor.nuts.lib.model;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ControllerInfo {
    public String mClassName;

    public transient Object mObject;

    public Object callFromJson(final String methodName, final GsonObject[] gsonArgs) throws ClassNotFoundException, InvocationTargetException, IllegalAccessException {
        final Object[] args = new Object[gsonArgs.length];
        for (int i = 0; i < gsonArgs.length; i++) {
            GsonObject gsonObject = gsonArgs[i];
            if ("null".equals(gsonObject.mClass)) {
                args[i] = null;
            } else {
                args[i] = gsonObject.toObj();
            }
        }
        Method method = null;
        for (Method m : mObject.getClass().getDeclaredMethods()) {
            if (m.getName().equals(methodName) && m.getTypeParameters().length == gsonArgs.length) {
                method = m;
            }
        }
        if (method == null) {
            throw new Error("");
        }
        return method.invoke(mObject, args);
    }
}
