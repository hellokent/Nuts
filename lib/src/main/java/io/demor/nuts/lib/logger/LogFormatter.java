package io.demor.nuts.lib.logger;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.demor.nuts.lib.annotation.log.LogFormatKeyword;

import java.lang.reflect.Field;
import java.util.*;

public final class LogFormatter<T> {

    private static final HashMap<Class<?>, HashMap<String, Field>> CLASS_CACHE = Maps.newHashMap();
    String mFormat;
    private ArrayList<Field> mFields = Lists.newArrayList();
    private Object[] mArgArray;
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
                if ("s".equals(keyword.value())) {
                    throw new IllegalStateException("Illegal keyword : s");
                }
                fieldMap.put("%" + keyword.value(), f);
                f.setAccessible(true);
            }
        }

        String newFormat = format;
        final TreeSet<KeywordLocation> locationSet = Sets.newTreeSet();

        for (Map.Entry<String, Field> entry : fieldMap.entrySet()) {
            final String key = entry.getKey();

            int index;
            while ((index = newFormat.indexOf(key)) >= 0) {
                locationSet.add(new KeywordLocation(index, entry));
                newFormat = newFormat.substring(0, index) + "%s" + newFormat.substring(index + key.length());
                mKeySet.add(key);
            }
        }

        for (KeywordLocation location : locationSet) {
            mFields.add(location.mEntry.getValue());
        }

        mFormat = newFormat;
        mArgArray = new String[mFields.size()];
    }

    public String format(T t) {
        for (int i = 0; i < mFields.size(); ++i) {
            try {
                final Object o = mFields.get(i).get(t);
                mArgArray[i] = o == null ? "" : o.toString();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return String.format(mFormat, mArgArray);
    }

    public boolean containsKey(final String key) {
        return mKeySet.contains("%" + key);
    }

    private static class KeywordLocation implements Comparable<KeywordLocation> {
        int mIndex;
        Map.Entry<String, Field> mEntry;

        KeywordLocation(int index, Map.Entry<String, Field> entry) {
            mIndex = index;
            mEntry = entry;
        }

        @Override
        public int compareTo(KeywordLocation another) {
            final int lhs = mIndex;
            final int rhs = another.mIndex;
            return lhs < rhs ? -1 : (lhs == rhs ? 0 : 1);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            KeywordLocation location = (KeywordLocation) o;
            return mIndex == location.mIndex;

        }

        @Override
        public int hashCode() {
            return mIndex;
        }
    }

}
