package io.demor.nuts.lib;

import android.os.Handler;
import android.os.Looper;
import io.demor.nuts.lib.eventbus.EventBus;

public interface Globals {
    Handler UI_HANDLER = new Handler(Looper.getMainLooper());
    EventBus BUS = new EventBus();
}
