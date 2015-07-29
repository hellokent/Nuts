package com.nuts.lib;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class NutsApplication extends Application {
    static NutsApplication sApplication;

    public static NutsApplication getGlobalContext() {
        return sApplication;
    }

    public static <T> T getService(String name) {
        return (T) getGlobalContext().getSystemService(name);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sApplication = this;
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
