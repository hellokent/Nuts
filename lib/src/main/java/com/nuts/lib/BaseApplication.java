package com.nuts.lib;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * Created by demor on 10/9/14.
 */
public abstract class BaseApplication extends Application {
    static BaseApplication sBaseApplication;

    public static BaseApplication getGlobalContext() {
        return sBaseApplication;
    }

    public static <T> T getService(String name) {
        return (T) getGlobalContext().getSystemService(name);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sBaseApplication = this;
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
