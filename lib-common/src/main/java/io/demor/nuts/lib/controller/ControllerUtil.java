package io.demor.nuts.lib.controller;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
//            .registerTypeAdapter(LocalApiResponse.class, new TypeAdapter<LocalApiResponse>() {
//                @Override
//                public void write(final JsonWriter out, final LocalApiResponse value) throws IOException {
//                    if (value == null) {
//                        out.nullValue();
//                    } else {
//                        out.beginObject()
//                                .name("code").value(value.code)
//                                .name("message").value(value.message)
//                                .name("clz").value(value.data == null ? "null" : value.data.getClass().getName())
//                                .name("data").value(value.data == null ? "null" : ControllerUtil.GSON.toJson(value.data))
//                                .endObject();
//                    }
//                }
//
//                @Override
//                public LocalApiResponse read(final JsonReader in) throws IOException {
//                    final LocalApiResponse result = new LocalApiResponse();
//                    in.beginObject();
//                    String clz = null;
//                    while (in.hasNext()) {
//                        switch (in.nextName()) {
//                            case "code":
//                                result.code = in.nextInt();
//                                break;
//                            case "message":
//                                result.message = in.nextString();
//                                break;
//                            case "clz":
//                                clz = in.nextString();
//                                break;
//                            case "data":
//                                String content = in.nextString();
//                                if ("null".equals(clz)) {
//                                    result.data = null;
//                                } else {
//                                    try {
//                                        result.data = ControllerUtil.GSON.fromJson(content, Class.forName(clz));
//                                    } catch (ClassNotFoundException e) {
//                                        throw new Error(e);
//                                    }
//                                }
//                                break;
//                        }
//                    }
//                    in.endObject();
//                    return result;
//                }
//            })
            .serializeNulls()
            .create();

    public static String generateControllerMethod(Method method, Object[] args) {
        final ControllerMethodInfo info = new ControllerMethodInfo();
        info.mName = method.getName();
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

    public static String callControllerNative(Object impl, String content) throws
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
        final Method m = impl.getClass().getInterfaces()[0].getDeclaredMethod(info.mName, argTypeArray);
        return toJson(Reflect.on(impl)
                .call(info.mName, argArray)
                .call("sync")
                .get(), (Class<?>) ReflectUtils.getGenericType(m.getGenericReturnType()));
    }

    public static String toJson(Object obj, Class<?> clz) {
        return GSON.toJson(obj, clz);
    }

    public static Object fromJson(String json, Class<?> clz) {
        return GSON.fromJson(json, clz);
    }

    public static class ControllerMethodInfo {
        public String mName;
        public String[] mArgsType;
        public String[] mArgs;
    }
}
