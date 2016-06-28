package io.demor.nuts.test.utils;

import android.app.Application;
import dalvik.system.DexFile;

import java.io.IOException;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

public class ClassIterator {

    public static List<String> scanClass(final Application app) throws IOException {

        final LinkedList<String> appSrcpaths = new LinkedList<>();

        //get class pathes
        String sourcePath = app.getApplicationInfo().sourceDir;
        DexFile dexfile = new DexFile(sourcePath);
        Enumeration<String> entries = dexfile.entries();
        while (entries.hasMoreElements()) {
            appSrcpaths.add(entries.nextElement());
        }

        return appSrcpaths;
    }
}
