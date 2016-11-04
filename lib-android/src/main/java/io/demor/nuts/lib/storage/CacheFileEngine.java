package io.demor.nuts.lib.storage;

import android.content.Context;
import io.demor.nuts.lib.NutsApplication;

import java.io.File;

public class CacheFileEngine extends FileEngine{

    public CacheFileEngine() {
        this("json", NutsApplication.getGlobalContext());
    }

    public CacheFileEngine(Context context) {
        this("json", context);
    }

    public CacheFileEngine(String dirName) {
        this(dirName, NutsApplication.getGlobalContext());
    }

    public CacheFileEngine(String dirName, Context context) {
        super(new File(context.getCacheDir(), dirName));
    }

}
