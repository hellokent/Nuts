package com.nuts.lib;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

import static com.nuts.lib.Globals.CLONER;
import static com.nuts.lib.Globals.GSON;

/**
 * 基于SharePreference的存储
 * Created by 陈阳(chenyang@edaijia-staff.cn>)
 * Date: 6/13/14 10:35 AM.
 */
public class Storage<T> {

    public static final HashMap<String, Object> CACHE_MAP = new HashMap<String, Object>();
    static final Application APP = BaseApplication.getGlobalContext();
    static final SharedPreferences SP = APP.getSharedPreferences("json", Context.MODE_PRIVATE);

    Class<T> mClass;
    String mKey;

    public Storage(Class<T> clz) {
        this(clz, clz.getName());
    }

    public Storage(Class<T> clz, String key) {
        mClass = clz;
        mKey = key;
    }

    public static synchronized void saveJson(String key, Object o) {
        if (o == null) {
            return;
        }
        CACHE_MAP.put(key, o);
        SP.edit().putString(key, GSON.toJson(o)).commit();
    }

    public static <T> T getJson(String key, Class<T> clz) {
        Object o = CACHE_MAP.get(key);
        if (o == null) {
            o = GSON.fromJson(SP.getString(key, ""), clz);
            CACHE_MAP.put(key, o);
        }
        return (T) o;
    }

    public static synchronized void delete(String key) {
        CACHE_MAP.remove(key);
        SP.edit().remove(key).commit();
    }

    public boolean contains() {
        return SP.contains(mKey);
    }

    public T get() {
        return CLONER.deepClone(getJson(mKey, mClass));
    }

    public void save(T t) {
        if (t == null) {
            return;
        }
        saveJson(mKey, t);
    }

    public void delete() {
        delete(mKey);
    }

}
