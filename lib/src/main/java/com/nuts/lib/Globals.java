package com.nuts.lib;

import android.os.Handler;
import android.os.Looper;

import com.nuts.lib.eventbus.EventBus;
import com.rits.cloning.Cloner;

public interface Globals {
    Handler UI_HANDLER = new Handler(Looper.getMainLooper());

    Cloner CLONER = new Cloner();

    EventBus BUS = new EventBus();
}
