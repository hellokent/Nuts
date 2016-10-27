package io.demor.nuts.sample;

import io.demor.nuts.lib.NutsApplication;
import io.demor.nuts.sample.config.Const;
import io.demor.nuts.sample.lib.controller.TestController;

public class GlobalApplication extends NutsApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        initApiServer()
                .registerController(TestController.class, Const.TEST_CONTROLLER)
                .start(8080);
    }
}
