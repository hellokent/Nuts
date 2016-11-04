package io.demor.nuts.lib;

import android.os.Handler;
import android.os.Looper;
import com.rits.cloning.Cloner;
import io.demor.nuts.lib.eventbus.EventBus;

public interface Globals {
    Handler UI_HANDLER = new Handler(Looper.getMainLooper());

    Cloner CLONER = new Cloner();

    EventBus BUS = new EventBus();

}
