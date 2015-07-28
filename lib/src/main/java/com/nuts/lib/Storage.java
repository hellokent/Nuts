package com.nuts.lib;

import java.util.HashMap;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.nuts.lib.storage.IStorageEngine;
import com.nuts.lib.storage.SharedPerferenceStorageEngine;
import static com.nuts.lib.Globals.CLONER;

public class Storage<T> {

    private static final HashMap<String, Object> CACHE_MAP = Maps.newHashMap();

    IStorageEngine mStorageEngine = new SharedPerferenceStorageEngine();

    Class<T> mClass;

    String mKey;

    Gson mGson;

    private Storage() {
    }

    public Storage(Class<T> clz) {
        this(null, clz);
    }

    public Storage(Gson gson, Class<T> clz) {
        this(gson, clz, clz.getName());
    }

    public Storage(Gson gson, Class<T> clz, String key) {
        mClass = clz;
        mKey = key;
        mGson = gson == null ? new Gson() : gson;
    }

    public synchronized boolean contains() {
        return get() != null;
    }

    public synchronized T get() {
        T o = (T) CACHE_MAP.get(mKey);
        if (o == null) {
            o = mGson.fromJson(mStorageEngine.get(mKey), mClass);
            CACHE_MAP.put(mKey, o);
        }
        return CLONER.deepClone(o);
    }

    public synchronized void save(T t) {
        if (t == null) {
            return;
        }
        CACHE_MAP.put(mKey, t);
        mStorageEngine.set(mKey, mGson.toJson(t));
    }

    public synchronized void delete() {
        CACHE_MAP.remove(mKey);
        mStorageEngine.delete(mKey);
    }

    public static class Builder<R> {

        private IStorageEngine mStorageEngine = new SharedPerferenceStorageEngine();

        private Class<R> mClass;

        private String mKey;

        private Storage<R> mStorage = new Storage<R>();

        public Builder<R> setClass(Class<R> clz) {
            mStorage.mClass = clz;
            return this;
        }

        public Builder<R> setName(String name) {
            mStorage.mKey = name;
            return this;
        }

        public Builder<R> setStorageEngine(IStorageEngine engine) {
            mStorage.mStorageEngine = engine;
            return this;
        }

        public Storage<R> build() {
            return mStorage;
        }
    }
}