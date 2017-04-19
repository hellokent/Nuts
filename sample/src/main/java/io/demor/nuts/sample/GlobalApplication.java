package io.demor.nuts.sample;

import io.demor.nuts.lib.NutsApplication;
import io.demor.nuts.lib.log.LoggerFactory;
import io.demor.nuts.sample.lib.controller.TestController;
import io.demor.server.WebDebug;

import static io.demor.nuts.sample.config.Const.*;

public class GlobalApplication extends NutsApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        initApiServer()
                .registerController(TestController.class, TEST_CONTROLLER)
                .registerEventBus(BUS)
                .registerListenBus()
                .registerStorage(SIMPLE_OBJECT_STORAGE)
                .start();
        WebDebug.init(this, 8888);
        LoggerFactory.readConfigFromAsset(this, "log.xml");
    }
}
