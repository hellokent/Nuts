package io.demor.nuts.lib.storage;

import android.content.Context;
import android.content.SharedPreferences;

import io.demor.nuts.lib.NutsApplication;

public class SharedPreferenceStorageEngine implements IStorageEngine {

    final SharedPreferences mSp;

    public SharedPreferenceStorageEngine() {
        this(NutsApplication.getGlobalContext());
    }

    public SharedPreferenceStorageEngine(Context context) {
        mSp = context.getSharedPreferences("json", Context.MODE_PRIVATE);
    }

    @Override
    public void set(final String key, final String value) {
        mSp.edit()
                .putString(key, value)
                .commit();
    }

    @Override
    public String get(final String key) {
        return mSp.getString(key, "");
    }

    @Override
    public void delete(final String key) {
        mSp.edit()
                .remove(key)
                .commit();
    }

    @Override
    public boolean contains(final String key) {
        return mSp.contains(key);
    }
}
