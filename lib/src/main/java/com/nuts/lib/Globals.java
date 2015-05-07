package com.nuts.lib;

import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;
import com.nuts.lib.eventbus.EventBus;
import com.rits.cloning.Cloner;

/**
 * Created by demor on 10/9/14.
 */
public interface Globals {
    Handler UI_HANDLER = new Handler(Looper.getMainLooper());

    Gson GSON = BaseApplication.getGlobalContext().getGson();

    Cloner CLONER = new Cloner();

    EventBus BUS = new EventBus();
}
