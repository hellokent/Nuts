package io.demor.nuts.lib;

import android.os.Handler;
import android.os.Looper;
import com.rits.cloning.Cloner;
import io.demor.nuts.lib.controller.Return;
import io.demor.nuts.lib.controller.ReturnImpl;
import io.demor.nuts.lib.controller.VoidReturn;
import io.demor.nuts.lib.controller.VoidReturnImpl;
import io.demor.nuts.lib.eventbus.EventBus;

import java.util.HashMap;

public interface Globals {
    Handler UI_HANDLER = new Handler(Looper.getMainLooper());

    Cloner CLONER = new Cloner();

    EventBus BUS = new EventBus();

    HashMap<Class, Class> RETURN_CAST_MAP = new HashMap<Class, Class>() {
        {
            put(Return.class, ReturnImpl.class);
            put(VoidReturn.class, VoidReturnImpl.class);
        }
    };
}
