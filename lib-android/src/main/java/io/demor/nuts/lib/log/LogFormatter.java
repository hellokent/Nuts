package io.demor.nuts.lib.log;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.demor.nuts.lib.annotation.log.LogFormatKeyword;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public final class LogFormatter<T> {

    private static final HashMap<Class<?>, HashMap<String, Field>> CLASS_CACHE = Maps.newHashMap();
    private List<Object> mFormatObjectList = Lists.newLinkedList();
    private HashSet<String> mKeySet = Sets.newHashSet();

    public LogFormatter(final String format, final Class<T> clz) {
        final HashMap<String, Field> fieldMap;
        if (CLASS_CACHE.containsKey(clz)) {
            fieldMap = CLASS_CACHE.get(clz);
        } else {
            fieldMap = Maps.newHashMap();
            for (Field f : clz.getDeclaredFields()) {
                final LogFormatKeyword keyword = f.getAnnotation(LogFormatKeyword.class);
                if (keyword == null) {
                    continue;
                }
                fieldMap.put(keyword.value(), f);
                f.setAccessible(true);
            }
        }

        for (int i = 0, j = 0, n = format.length() + 1; i < n; ++i) {
            if (i == n - 1) {
                mFormatObjectList.add(format.substring(j, i));
                break;
            }
            if (format.charAt(i) != '%') {
                continue;
            }

            for (Map.Entry<String, Field> fieldEntry : fieldMap.entrySet()) {
                final String name = fieldEntry.getKey();
                if (format.startsWith(name, i + 1)) {
                    mKeySet.add(name);
                    if (i != j) {
                        mFormatObjectList.add(format.substring(j, i));
                    }
                    mFormatObjectList.add(fieldEntry.getValue());
                    i += name.length();
                    j = i + 1;
                    break;
                }
            }
        }
    }

    public String format(T t) {
        final StringBuilder builder = new StringBuilder();
        for (Object o : mFormatObjectList) {
            if (o instanceof String) {
                builder.append((String) o);
            } else {
                Field field = (Field) o;
                try {
                    Object data = field.get(t);
                    builder.append(data == null ? "null" : data.toString());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return builder.toString();
    }

    public boolean containsKey(final String key) {
        return mKeySet.contains(key);
    }

}
