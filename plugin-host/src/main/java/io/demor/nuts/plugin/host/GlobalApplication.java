package io.demor.nuts.plugin.host;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.PrintStreamPrinter;
import dalvik.system.DexFile;
import io.demor.nuts.lib.NutsApplication;
import io.demor.nuts.lib.log.L;
import org.joor.Reflect;

import java.io.File;
import java.lang.reflect.Array;

public class GlobalApplication extends NutsApplication {

    Resources resources;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        final ClassLoader loader = getClassLoader();
        Object[] hostElement = Reflect.on(loader)
                .field("pathList")
                .field("dexElements").get();

        File pluginFile = new File("/sdcard/plugin.apk");
        File optimizedDirectory = new File(base.getCacheDir(), "plugin");
        if (!optimizedDirectory.exists() && !optimizedDirectory.mkdir()) {
            L.v("failed create cache dir");
            return;
        }

        final Class<?> elementClz = hostElement[0].getClass();
        Object[] newElement = (Object[]) Array.newInstance(elementClz, hostElement.length + 1);
        for (int i = 0, n = hostElement.length; i < n; ++i) {
            Array.set(newElement, i + 1, hostElement[i]);
        }
        DexFile file = Reflect.on("dalvik.system.DexPathList")
                .call("loadDexFile", pluginFile, optimizedDirectory)
                .get();
        Object element = Reflect.on(elementClz)
                .create(pluginFile, false, pluginFile, file)
                .get();
        L.v("element.Type=%s", element.getClass().getName());
        Array.set(newElement, 0, element);

        Reflect.on(loader)
                .field("pathList")
                .set("dexElements", newElement);

        AssetManager assetManager = Reflect.on(AssetManager.class)
                .create()
                .get();

        int result = Reflect.on(assetManager)
                .call("addAssetPath", pluginFile.getPath())
                .get();

        resources = new Resources(assetManager, getResources().getDisplayMetrics(), getResources().getConfiguration());

    }

    @Override
    public void onCreate() {
        super.onCreate();
        getApplicationInfo().dump(new PrintStreamPrinter(System.out), "application");
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                L.v(resources.getString(R.string.app_name));
                if (activity.getClass().getName().equals("io.demor.nuts.plugin.PluginActivity")) {
                    Reflect.on(activity)
                            .set("mResources", resources);
                }
            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });


    }
}
