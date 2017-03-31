package io.demor.nuts.sample.ui;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import io.demor.nuts.lib.log.Logger;
import io.demor.nuts.lib.log.LoggerFactory;

public class DemoService extends Service {

    static final Logger LOGGER = LoggerFactory.getLogger(DemoService.class);

    @Override
    public void onCreate() {
        super.onCreate();
        LOGGER.v("Demo Service onCreate");
    }

    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }
}
