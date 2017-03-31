package io.demor.nuts.sample;

import io.demor.nuts.lib.NutsApplication;
import io.demor.nuts.lib.log.LoggerFactory;
import io.demor.nuts.sample.config.Const;
import io.demor.nuts.sample.lib.controller.TestController;
import io.demor.nuts.sample.lib.module.SimpleObject;
import io.demor.server.WebDebug;

public class GlobalApplication extends NutsApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        initApiServer()
                .registerController(TestController.class, Const.TEST_CONTROLLER)
                .registerEventBus(Const.BUS)
                .registerListenBus()
                .registerStorage(SimpleObject.class, Const.SIMPLE_OBJECT_STORAGE)
                .start();
        WebDebug.init(this, 8888);
        LoggerFactory.readConfigFromAsset(this, "log.xml");
    }
}
