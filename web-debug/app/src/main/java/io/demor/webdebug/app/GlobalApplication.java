package io.demor.webdebug.app;

import android.app.Application;
import io.demor.server.ServerManager;

public class GlobalApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ServerManager.init(this);
    }
}
