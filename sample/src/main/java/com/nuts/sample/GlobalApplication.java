package com.nuts.sample;

import com.google.gson.Gson;
import com.nuts.lib.BaseApplication;
import com.nuts.sample.config.Const;

public class GlobalApplication extends BaseApplication {

    @Override
    public Gson getGson() {
        return Const.GSON;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
