package io.demor.nuts.lib;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import io.demor.nuts.common.server.ApiServer;

public class NutsApplication extends Application {
    static NutsApplication sApplication;
    static ApiServer mApiServer;

    public static NutsApplication getGlobalContext() {
        return sApplication;
    }

    public static String getIpAddress() {
        if (mApiServer != null) {
            return mApiServer.mServer.getIpAddress();
        } else {
            return "";
        }
    }

    public static int getHttpPort() {
        return mApiServer == null ? 0 : mApiServer.mServer.getHttpPort();
    }

    public static int getWebSocketPort() {
        return mApiServer == null ? 0 : mApiServer.mServer.getWebSocketPort();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sApplication = this;
    }

    public ApiServer initApiServer() {
        mApiServer = new ApiServer(this);
        return mApiServer;
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
