package com.nuts.lib;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;

/**
 * 反射相关的工具方法
 * Created by chenyang on 14-2-7.
 */
public final class ReflectUtils {
    private ReflectUtils() {
    }

    /**
     * 判断type是否是superType的子类型
     *
     * @param type       子类型
     * @param superClass 父类型
     * @return 假如有父子关系，或者子类型实现了父类型的接口，返回true，否则返回false
     */
    public static boolean isSubclassOf(Class<?> type, Class<?> superClass) {
        if (type == null) {
            return false;
        }
        if (type.equals(superClass)) {
            return true;
        }
        Class[] interfaces = type.getInterfaces();
        for (Class i : interfaces) {
            if (isSubclassOf(i, superClass)) {
                return true;
            }
        }
        Class superType = type.getSuperclass();
        return superType != null && isSubclassOf(superType, superClass);
    }

    public static <T> T newInstance(final Class<T> clazz, Object... arg) throws
            IllegalAccessException,
            InstantiationException,
            NoSuchMethodException,
            InvocationTargetException {
        if (clazz == null) {
            return null;
        }

        if (arg == null) {
            return clazz.newInstance();
        }

        Class[] argumentClasses = new Class[arg.length];

        for (int i = 0, n = arg.length; i < n; ++i) {
            argumentClasses[i] = arg[i].getClass();
        }

        return clazz.getDeclaredConstructor(argumentClasses).newInstance(arg);
    }

    public static Type getGenericType(final Type t) {
        if (t instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) t;
            return pt.getActualTypeArguments()[0];
        } else {
            return null;
        }
    }

    /**
     * 检查类型是否符合范型的要求
     *
     * @param paramType 范型Type
     * @param clz       待检查类型
     */
    public static boolean checkGenericType(final Type paramType, final Class clz) {
        if (paramType == null ||
                !(paramType instanceof ParameterizedType)) { // paramType不是范形
            return false;
        }
        ParameterizedType pt = (ParameterizedType) paramType;
        Type actual = pt.getActualTypeArguments()[0];
        if (actual instanceof Class) {
            return actual == clz;
        } else if (actual instanceof WildcardType) {
            WildcardType wildcardType = (WildcardType) actual;
            Type[] typeArray = wildcardType.getLowerBounds();
            if (typeArray != null && typeArray.length != 0) {
                return isSubclassOf((Class) typeArray[0], clz);
            }
            typeArray = wildcardType.getUpperBounds();
            if (typeArray != null && typeArray.length != 0) {
                return isSubclassOf(clz, (Class) typeArray[0]);
            }
        }
        return false;
    }


}
