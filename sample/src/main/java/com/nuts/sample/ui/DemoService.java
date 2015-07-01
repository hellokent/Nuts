package com.nuts.sample.ui;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.nuts.lib.log.L;

public class DemoService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
        L.v("Demo Service onCreate");
    }

    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }
}
