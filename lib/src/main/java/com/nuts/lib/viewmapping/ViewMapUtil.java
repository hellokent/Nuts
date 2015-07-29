package com.nuts.lib.viewmapping;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Field;

import com.nuts.lib.NutsApplication;
import com.nuts.lib.annotation.viewmapping.ViewMapping;

public final class ViewMapUtil {

    public static Application sApp = NutsApplication.getGlobalContext();


    /**
     * @param object   要映射对象
     * @param rootView 要映射对象所要查询的根控件
     */
    public static void map(Object object, View rootView) {
        Class<?> clazz = object.getClass();

        while (clazz != null &&
                clazz != Activity.class &&
                clazz != View.class) {
            for (Field f : clazz.getDeclaredFields()) {
                ViewMapping mapping = f.getAnnotation(ViewMapping.class);
                int id = 0;
                if (mapping == null) {
                    continue;
                }
                try {
                    id = mapping.value();
                    f.setAccessible(true);
                    View childView = rootView.findViewById(id);
                    if (childView == null) {
                        continue;
                    }
                    f.set(object, childView);
                } catch (Exception e) {
                    System.err.println(String.format(
                            "view map error = %h, clazz:%s, field:%s", id,
                            clazz.getSimpleName(), f.getName()));
                    e.printStackTrace();
                    throw new RuntimeException();
                }
            }
            clazz = clazz.getSuperclass();
        }
    }

    public static View map(Object object) {
        final LayoutInflater inflater;
        if (object instanceof Activity) {
            inflater = ((Activity) object).getLayoutInflater();
        } else {
            inflater = (LayoutInflater) sApp.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        return map(object, inflater, null);
    }

    public static View map(Object object, int layoutId) {
        final LayoutInflater inflater;
        if (object instanceof Activity) {
            inflater = ((Activity) object).getLayoutInflater();
        } else {
            inflater = (LayoutInflater) sApp.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        final View root = inflater.inflate(layoutId, null, false);
        map(object, root);
        return root;
    }

    public static View map(Object object, LayoutInflater inflater,
                           ViewGroup root) {
        View rootView = inflater.inflate(getViewMapping(object.getClass()).value(), root, false);
        map(object, rootView);
        return rootView;
    }

    public static View mapForMerge(Object object, int layoutId,
                                   ViewGroup view) {
        View rootView = ((LayoutInflater) sApp.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
                layoutId, view);
        map(object, rootView);
        return rootView;
    }

    /**
     * 根据ViewHolder的Class对象，新建一个ViewHolder类和对应Layout的View对象
     *
     * @return Pair.first是对应的ViewHolder，Pair.second是ViewHolder注解里面的Layout对应的View
     */
    public static <T> Pair<T, View> map(Class<T> clazz,
                                        LayoutInflater inflater, ViewGroup root) {
        Pair<T, View> pair = null;
        T object;
        try {
            object = (T) clazz.getConstructor(new Class[]{}).newInstance();
            View rootView = inflater.inflate(getViewMapping(clazz).value(),
                    root, false);
            pair = Pair.create(object, rootView);
            map(object, rootView);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pair;
    }

    public static <T> Pair<T, View> mapForConvert(Class<T> clazz,
                                                  View convertView,
                                                  ViewGroup parentView) {
        final T $t;
        if (convertView != null) {
            $t = (T) convertView.getTag();
        } else {
            Pair<T, View> pair = map(clazz,
                    (LayoutInflater) sApp.getSystemService(Context.LAYOUT_INFLATER_SERVICE), parentView);
            $t = pair.first;
            convertView = pair.second;
            convertView.setTag($t);
        }
        return Pair.create($t, convertView);
    }

    static ViewMapping getViewMapping(Class<?> clazz) {
        ViewMapping mapping;

        while ((!Activity.class.equals(clazz))
                && clazz != null) {
            mapping = clazz.getAnnotation(ViewMapping.class);
            if (mapping != null) {
                return mapping;
            } else {
                clazz = clazz.getSuperclass();
            }
        }
        return null;
    }
}
