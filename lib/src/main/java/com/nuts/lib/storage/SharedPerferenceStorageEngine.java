package com.nuts.lib.storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.nuts.lib.BaseApplication;

public class SharedPerferenceStorageEngine implements IStorageEngine {

    static final SharedPreferences SP = BaseApplication.getGlobalContext()
            .getSharedPreferences("json", Context.MODE_PRIVATE);

    @Override
    public void set(final String key, final String value) {
        SP.edit()
                .putString(key, value)
                .commit();
    }

    @Override
    public String get(final String key) {
        return SP.getString(key, "");
    }

    @Override
    public void delete(final String key) {
        SP.edit()
                .remove(key)
                .commit();
    }

    @Override
    public boolean contains(final String key) {
        return SP.contains(key);
    }
}
