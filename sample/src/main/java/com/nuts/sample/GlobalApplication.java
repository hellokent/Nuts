package com.nuts.sample;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.nuts.lib.BaseApplication;
import com.nuts.lib.log.L;
import dalvik.system.PathClassLoader;

public class GlobalApplication extends BaseApplication {

    static final String LOADED_APK = "1.apk";

    public static boolean isRunningFromPlugin() {
        return isRunningFromPlugin("com.nuts.app");
    }

    public static boolean isRunningFromPlugin(String pluginPackageName) {
        for (StackTraceElement element : Thread.currentThread()
                .getStackTrace()) {
            if (element.getClassName()
                    .startsWith(pluginPackageName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //try {
        //    final Object loadApk = getField("mLoadedApk", this);
        //    final PathClassLoader originClassLoader = (PathClassLoader) getDeclaredField("mClassLoader", loadApk);
        //
        //    final ClassLoader classLoader = new ClassLoader(originClassLoader) {
        //        @Override
        //        protected Class<?> findClass(final String className) throws ClassNotFoundException {
        //            L.v("find Class:" + className);
        //            return super.findClass(className);
        //        }
        //
        //        @Override
        //        public URL getResource(final String resName) {
        //            L.v("get Resource:" + resName);
        //            return super.getResource(resName);
        //        }
        //
        //        @Override
        //        public Enumeration<URL> getResources(final String resName) throws IOException {
        //            L.v("get Resource2:" + resName);
        //            return super.getResources(resName);
        //        }
        //
        //        @Override
        //        public InputStream getResourceAsStream(final String resName) {
        //            L.v("get Resource As Stream:" + resName);
        //            return super.getResourceAsStream(resName);
        //        }
        //
        //        @Override
        //        public Class<?> loadClass(final String className) throws ClassNotFoundException {
        //            L.v("load Class:" + className);
        //            return super.loadClass(className);
        //        }
        //
        //        @Override
        //        protected Class<?> loadClass(final String className, final boolean resolve) throws
        // ClassNotFoundException {
        //            L.v("load Class2:%s, resolve:%s", className, resolve);
        //            return super.loadClass(className, resolve);
        //        }
        //
        //        @Override
        //        protected URL findResource(final String resName) {
        //            L.v("find Resource3:" + resName);
        //            return super.findResource(resName);
        //        }
        //
        //        @Override
        //        protected Enumeration<URL> findResources(final String resName) throws IOException {
        //            L.v("find Resources:" + resName);
        //            return super.findResources(resName);
        //        }
        //
        //        @Override
        //        protected String findLibrary(final String libName) {
        //            L.v("find Library:" + libName);
        //            return super.findLibrary(libName);
        //        }
        //
        //        @Override
        //        protected Package getPackage(final String name) {
        //            L.v("get package" + name);
        //            return super.getPackage(name);
        //        }
        //
        //        @Override
        //        protected Package[] getPackages() {
        //            return super.getPackages();
        //        }
        //
        //        @Override
        //        protected Package definePackage(final String name, final String specTitle, final String specVersion,
        //                                        final String specVendor, final String implTitle, final String
        //                                                implVersion, final String implVendor, final URL sealBase)
        // throws IllegalArgumentException {
        //            return super.definePackage(name, specTitle, specVersion, specVendor, implTitle, implVersion,
        // implVendor, sealBase);
        //        }
        //
        //        @Override
        //        public void setClassAssertionStatus(final String cname, final boolean enable) {
        //            super.setClassAssertionStatus(cname, enable);
        //        }
        //
        //        @Override
        //        public void setPackageAssertionStatus(final String pname, final boolean enable) {
        //            super.setPackageAssertionStatus(pname, enable);
        //        }
        //
        //        @Override
        //        public void setDefaultAssertionStatus(final boolean enable) {
        //            super.setDefaultAssertionStatus(enable);
        //        }
        //
        //        @Override
        //        public void clearAssertionStatus() {
        //            super.clearAssertionStatus();
        //        }
        //    };
        //
        //    setDeclaredField("mClassLoader", loadApk, classLoader);
        //} catch (NoSuchFieldException | IllegalAccessException e) {
        //    e.printStackTrace();
        //}

        ApplicationInfo info = getApplicationInfo();
        L.v("application info resDir=%s", info.sourceDir);
    }

    Object getField(String name, Object obj) throws NoSuchFieldException, IllegalAccessException {
        Field field = null;
        for (Field f : obj.getClass()
                .getFields()) {
            if (f.getName()
                    .equals(name)) {
                field = f;
                break;
            }
        }
        if (field == null) {
            field = obj.getClass()
                    .getField(name);
        }
        field.setAccessible(true);
        return field.get(obj);
    }

    Object getSuperDeclaredField(String name, Object obj) throws NoSuchFieldException, IllegalAccessException {
        Field field = null;
        for (Field f : obj.getClass()
                .getSuperclass()
                .getDeclaredFields()) {
            if (f.getName()
                    .equals(name)) {
                field = f;
                break;
            }
        }
        if (field == null) {
            field = obj.getClass()
                    .getSuperclass()
                    .getDeclaredField(name);
        }
        field.setAccessible(true);
        return field.get(obj);
    }

    Object getDeclaredField(String name, Object obj) throws NoSuchFieldException, IllegalAccessException {
        Field field = null;
        for (Field f : obj.getClass()
                .getDeclaredFields()) {
            if (f.getName()
                    .equals(name)) {
                field = f;
                break;
            }
        }
        if (field == null) {
            field = obj.getClass()
                    .getDeclaredField(name);
        }
        field.setAccessible(true);
        return field.get(obj);
    }

    void setDeclaredField(String name, Object obj, Object value) throws NoSuchFieldException, IllegalAccessException {
        final Field f = obj.getClass()
                .getDeclaredField(name);
        f.setAccessible(true);
        f.set(obj, value);
    }

    void setSuperDeclaredField(String name, Object obj, Object value) throws NoSuchFieldException,
            IllegalAccessException {
        final Field f = obj.getClass()
                .getSuperclass()
                .getDeclaredField(name);
        f.setAccessible(true);
        f.set(obj, value);
    }

    @Override
    protected void attachBaseContext(final Context base) {
        super.attachBaseContext(base);
        final PathClassLoader originClassLoader = (PathClassLoader) getClassLoader();
        L.v("get origin CL: %s", originClassLoader.getClass()
                .getName());
        File file = getFilesDir();
        File apkFile = new File(file, LOADED_APK);
        if (!apkFile.exists() || apkFile.length() == 0 || true) {
            try {
                Files.copy(new File("/sdcard/" + LOADED_APK), apkFile);
            } catch (IOException e) {
                L.exception(e);
            }
        }
        L.v("apk file length=%s", apkFile.length());

        try {
            Class pathListClz = Class.forName("dalvik.system.DexPathList");
            Method method = pathListClz.getDeclaredMethod("makeDexElements", ArrayList.class, File.class, ArrayList
                    .class);
            method.setAccessible(true);
            Object elementArray = method.invoke(pathListClz, Lists.newArrayList(apkFile), apkFile.getParentFile(),
                    Lists.newArrayList());
            L.v("array=%s", elementArray.getClass()
                    .getName());

            Object pathList = getSuperDeclaredField("pathList", originClassLoader);

            Object origElementArray = getDeclaredField("dexElements", pathList);

            Object resultArray = Array.newInstance(Array.get(origElementArray, 0)
                    .getClass(), Array.getLength(origElementArray) + Array.getLength(elementArray));
            int i = 0;
            for (int j = 0, n = Array.getLength(origElementArray); j < n; ++i, ++j) {
                Array.set(resultArray, i, Array.get(origElementArray, j));
            }

            for (int j = 0, n = Array.getLength(elementArray); j < n; ++i, ++j) {
                Array.set(resultArray, i, Array.get(elementArray, j));
            }

            setDeclaredField("dexElements", pathList, resultArray);
            L.v("success load classes");

            // loadResources(apkFile.getAbsolutePath());
            L.v("success load resources");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void loadResources(String path) {
        L.v("load resources:%s", path);
        try {
            AssetManager assetManager = AssetManager.class.newInstance();
            //assetManager.getClass()
            //        .getMethod("addAssetPath", String.class)
            //        .invoke(assetManager, getApplicationInfo().sourceDir);
            assetManager.getClass()
                    .getMethod("addAssetPath", String.class)
                    .invoke(assetManager, path);

            final Resources superRes = getResources();
            final Resources pluginRes = new Resources(assetManager, superRes.getDisplayMetrics(), superRes
                    .getConfiguration());

            final Resources resources = new Resources(getAssets(), superRes.getDisplayMetrics(), superRes
                    .getConfiguration()) {

                @Override
                public CharSequence getText(final int id) throws NotFoundException {
                    if (isRunningFromPlugin()) {
                        return pluginRes.getText(id);
                    }
                    return super.getText(id);
                }

            };
            //
            //Field loadApkField = getBaseContext().getClass().getDeclaredField("mPackageInfo");
            //loadApkField.setAccessible(true);
            //Object loadApk = loadApkField.get(getBaseContext());
            //setDeclaredField("mResources", loadApk, resources);

        } catch (Exception e) {
            e.printStackTrace();
            L.exception(e);
        }
    }
}
