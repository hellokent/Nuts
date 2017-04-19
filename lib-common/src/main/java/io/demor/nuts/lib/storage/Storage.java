package io.demor.nuts.lib.storage;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.rits.cloning.Cloner;
import io.demor.nuts.lib.controller.MethodInfoUtil;

import java.util.Map;

public class Storage<T> {

    public static final Cloner CLONER = new Cloner();
    private static final Map<String, Object> CACHE_MAP = Maps.newConcurrentMap();

    private IStorageEngine mStorageEngine;

    private Class<T> mClass;

    private String mKey = null;

    private Gson mGson = MethodInfoUtil.GSON;

    protected Storage() {
    }

    public synchronized boolean contains() {
        return mStorageEngine.contains(mKey);
    }

    public Class<T> getObjectClass() {
        return mClass;
    }

    public String getKey() {
        return mKey;
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

        private Storage<R> mStorage = new Storage<>();

        public Builder<R> setGson(Gson gson) {
            mStorage.mGson = gson;
            return this;
        }

        public Builder<R> setClass(Class<R> clz) {
            mStorage.mClass = clz;
            if (Strings.isNullOrEmpty(mStorage.mKey)) {
                mStorage.mKey = clz.getName();
            }
            return this;
        }

        public Builder<R> setKey(String key) {
            mStorage.mKey = key;
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