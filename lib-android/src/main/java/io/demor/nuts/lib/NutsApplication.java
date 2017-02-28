package io.demor.nuts.lib;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import io.demor.nuts.lib.server.impl.ApiServer;

public class NutsApplication extends Application {
    public static NutsApplication sApplication;
    public static ApiServer sApiServer;

    public static NutsApplication getGlobalContext() {
        return sApplication;
    }

    public static String getIpAddress() {
        if (sApiServer != null) {
            return sApiServer.getIpAddress();
        } else {
            return "";
        }
    }

    public static int getHttpPort() {
        return sApiServer == null ? 0 : sApiServer.getHttpPort();
    }

    public static int getWebSocketPort() {
        return sApiServer == null ? 0 : sApiServer.getWebSocketPort();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sApplication = this;
    }

    public ApiServer initApiServer() {
        sApiServer = new ApiServer(this);
        return sApiServer;
    }

    public final int getVersionCode() {
        try {
            final PackageInfo pi = getPackageManager().getPackageInfo(getPackageName(), 0);
            return pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public final String getVersionName() {
        try {
            final PackageInfo pi = getPackageManager().getPackageInfo(getPackageName(), 0);
            return pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }
}
