package com.nuts.lib.log;

import android.util.Log;

enum LogLevel {
    VERBOSE(Log.VERBOSE),
    DEBUG(Log.DEBUG),
    INFO(Log.INFO),
    WARN(Log.WARN),
    ERROR(Log.ERROR),
    ASSERT(Log.ASSERT);

    private final int mLevel;

    LogLevel(int level) {
        mLevel = level;
    }

    public boolean isValidLevel(int level) {
        return level > mLevel;
    }
}
