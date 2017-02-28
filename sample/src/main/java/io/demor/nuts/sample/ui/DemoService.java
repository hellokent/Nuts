package io.demor.nuts.sample.ui;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import io.demor.nuts.lib.logger.Logger;
import io.demor.nuts.lib.logger.LoggerFactory;

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
