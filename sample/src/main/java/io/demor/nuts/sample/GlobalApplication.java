package io.demor.nuts.sample;

import io.demor.nuts.lib.NutsApplication;
import io.demor.nuts.sample.config.Const;
import io.demor.nuts.sample.lib.controller.TestController;
import io.demor.server.WebDebug;

public class GlobalApplication extends NutsApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        initApiServer()
                .registerController(TestController.class, Const.TEST_CONTROLLER)
                .registerEventBus(Const.BUS)
                .registerListenBus()
                .start();
        WebDebug.init(this, 8888);
    }
}
