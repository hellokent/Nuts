package io.demor.nuts.lib.controller;

import com.google.gson.*;
import io.demor.nuts.lib.ReflectUtils;
import org.joor.Reflect;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class ControllerUtil {

    public static final Gson GSON = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES)
            .setFieldNamingStrategy(new FieldNamingStrategy() {
                @Override
                public String translateName(Field f) {
                    String name = f.getName();
                    if (name.length() > 1 && name.startsWith("m") && Character.isUpperCase(name.charAt(1))) {
                        return ("" + name.charAt(1)).toLowerCase() + name.substring(2);
                    } else {
                        return name;
                    }
                }
            })
            .serializeNulls()
            .create();

    public static String generateMethodInfo(Method method, Object[] args) {
        final ControllerMethodInfo info = new ControllerMethodInfo();
        info.mName = method.getName();
        info.mClz = method.getDeclaringClass().getName();
        info.mArgsType = new String[method.getParameterTypes().length];
        for (int i = 0; i < method.getParameterTypes().length; ++i) {
            final Class clz = method.getParameterTypes()[i];
            info.mArgsType[i] = clz.getName();
        }
        final int length = args == null ? 0 : args.length;
        info.mArgs = new String[length];
        for (int i = 0; i < length; ++i) {
            info.mArgs[i] = GSON.toJson(args[i]);
        }
        return GSON.toJson(info);
    }

    public static InvokeMethodInfo parseMethodInfo(Object impl, String content) throws
            NoSuchMethodException,
            InvocationTargetException,
            IllegalAccessException,
            ClassNotFoundException {

        final ControllerMethodInfo info = GSON.fromJson(content, ControllerMethodInfo.class);
        final Class[] argTypeArray = new Class[info.mArgsType.length];
        final Object[] argArray = new Object[info.mArgs.length];

        for (int i = 0; i < info.mArgsType.length; ++i) {
            switch (info.mArgsType[i]) {
                case "boolean":
                    argTypeArray[i] = boolean.class;
                    break;
                case "char":
                    argTypeArray[i] = char.class;
                    break;
                case "byte":
                    argTypeArray[i] = byte.class;
                    break;
                case "short":
                    argTypeArray[i] = short.class;
                    break;
                case "int":
                    argTypeArray[i] = int.class;
                    break;
                case "long":
                    argTypeArray[i] = long.class;
                    break;
                case "float":
                    argTypeArray[i] = float.class;
                    break;
                case "double":
                    argTypeArray[i] = double.class;
                    break;
                case "void":
                    argTypeArray[i] = void.class;
                    break;
                default:
                    argTypeArray[i] = Class.forName(info.mArgsType[i]);
            }
        }

        for (int i = 0; i < info.mArgs.length; ++i) {
            argArray[i] = GSON.fromJson(info.mArgs[i], argTypeArray[i]);
        }

        final InvokeMethodInfo result = new InvokeMethodInfo();
        result.mClz = info.mClz;
        result.mName = info.mName;
        result.mArgs = info.mArgs;
        result.mArgsType = info.mArgsType;
        result.mArgArray = argArray;
        result.mImpl = impl;
        result.mArgTypeArray = argTypeArray;
        return result;
    }


    public static String toJson(Object obj, Class<?> clz) {
        if (obj == null) {
            return JsonNull.INSTANCE.toString();
        } else {
            return GSON.toJson(obj, clz);
        }
    }

    public static Object fromJson(String json, Class<?> clz) {
        return GSON.fromJson(json, clz);
    }

    public static class ControllerMethodInfo {
        public String mClz;
        public String mName;
        public String[] mArgsType;
        public String[] mArgs;
    }

    public static class InvokeMethodInfo extends ControllerMethodInfo {
        public Object[] mArgArray;
        public Class<?>[] mArgTypeArray;
        public Object mImpl;

        public String callController() {
            final Method m;
            try {
                m = mImpl.getClass().getInterfaces()[0].getDeclaredMethod(mName, mArgTypeArray);
            } catch (NoSuchMethodException e) {
                throw new IllegalArgumentException(e);
            }
            if (ReflectUtils.isSubclassOf(m.getReturnType(), Return.class)) {
                return toJson(Reflect.on(mImpl)
                        .call(mName, mArgArray)
                        .call("sync")
                        .get(), (Class<?>) ReflectUtils.getGenericType(m.getGenericReturnType()));
            } else {
                try {
                    m.setAccessible(true);
                    return toJson(m.invoke(mImpl, mArgArray), m.getReturnType());
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new IllegalStateException(e);
                }
            }
        }

        public Object callImpl() {
            return Reflect.on(mImpl)
                    .call(mName, mArgArray)
                    .get();
        }
    }
}
