package io.demor.nuts.lib.storage;

import com.google.common.collect.Maps;

import java.util.HashMap;

public class MemoryEngine implements IStorageEngine {

    public HashMap<String, String> mMap = Maps.newHashMap();

    @Override
    public void set(final String key, final String value) {
        mMap.put(key, value);
    }

    @Override
    public String get(final String key) {
        return mMap.get(key);
    }

    @Override
    public void delete(final String key) {
        mMap.remove(key);
    }

    @Override
    public boolean contains(final String key) {
        return mMap.containsKey(key);
    }
}
