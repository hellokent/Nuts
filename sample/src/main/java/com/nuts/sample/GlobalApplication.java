package com.nuts.sample;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Enumeration;

import com.nuts.lib.BaseApplication;
import dalvik.system.PathClassLoader;

public class GlobalApplication extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            final Object loadApk = getField(getClass(), "mLoadedApk", this);
            final PathClassLoader originClassLoader = (PathClassLoader) getDeclaredField(loadApk.getClass(),
                    "mClassLoader", loadApk);

            final ClassLoader classLoader = new ClassLoader(originClassLoader) {
                @Override
                protected Class<?> findClass(final String className) throws ClassNotFoundException {
                    Log.v("loader", "find Class:" + className);
                    return super.findClass(className);
                }

                @Override
                public URL getResource(final String resName) {
                    Log.v("loader", "get Resource:" + resName);
                    return super.getResource(resName);
                }

                @Override
                public Enumeration<URL> getResources(final String resName) throws IOException {
                    Log.v("loader", "get Resource2:" + resName);
                    return super.getResources(resName);
                }

                @Override
                public InputStream getResourceAsStream(final String resName) {
                    Log.v("loader", "get Resource As Stream:" + resName);
                    return super.getResourceAsStream(resName);
                }

                @Override
                public Class<?> loadClass(final String className) throws ClassNotFoundException {
                    Log.v("loader", "load Class:" + className);
                    return super.loadClass(className);
                }

                @Override
                protected Class<?> loadClass(final String className, final boolean resolve) throws ClassNotFoundException {
                    Log.v("loader", "load Class2:" + className);
                    return super.loadClass(className, resolve);
                }

                @Override
                protected URL findResource(final String resName) {
                    Log.v("loader", "find Resource3:" + resName);
                    return super.findResource(resName);
                }

                @Override
                protected Enumeration<URL> findResources(final String resName) throws IOException {
                    Log.v("loader", "find Resources:" + resName);
                    return super.findResources(resName);
                }

                @Override
                protected String findLibrary(final String libName) {
                    Log.v("loader", "find Library:" + libName);
                    return super.findLibrary(libName);
                }

                @Override
                protected Package getPackage(final String name) {
                    Log.v("loader", "get package" + name);
                    return super.getPackage(name);
                }

                @Override
                protected Package[] getPackages() {
                    return super.getPackages();
                }

                @Override
                protected Package definePackage(final String name, final String specTitle, final String specVersion,
                                                final String specVendor, final String implTitle, final String
                                                        implVersion, final String implVendor, final URL sealBase) throws IllegalArgumentException {
                    return super.definePackage(name, specTitle, specVersion, specVendor, implTitle, implVersion, implVendor, sealBase);
                }

                @Override
                public void setClassAssertionStatus(final String cname, final boolean enable) {
                    super.setClassAssertionStatus(cname, enable);
                }

                @Override
                public void setPackageAssertionStatus(final String pname, final boolean enable) {
                    super.setPackageAssertionStatus(pname, enable);
                }

                @Override
                public void setDefaultAssertionStatus(final boolean enable) {
                    super.setDefaultAssertionStatus(enable);
                }

                @Override
                public void clearAssertionStatus() {
                    super.clearAssertionStatus();
                }
            };

            setDeclaredField(loadApk.getClass(), "mClassLoader", loadApk, classLoader);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    Object getField(Class clz, String name, Object obj) throws NoSuchFieldException, IllegalAccessException {
        final Field f = clz.getField(name);
        f.setAccessible(true);
        return f.get(obj);
    }

    Object getDeclaredField(Class clz, String name, Object obj) throws NoSuchFieldException, IllegalAccessException {
        final Field f = clz.getDeclaredField(name);
        f.setAccessible(true);
        return f.get(obj);
    }

    void setDeclaredField(Class clz, String name, Object obj, Object value) throws NoSuchFieldException, IllegalAccessException {
        final Field f = clz.getDeclaredField(name);
        f.setAccessible(true);
        f.set(obj, value);
    }
}
