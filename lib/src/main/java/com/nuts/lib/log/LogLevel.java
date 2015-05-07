package com.nuts.lib.log;

import android.util.Log;

/**
 * Created by 陈阳(chenyang@edaijia-staff.cn>)
 * Date: 6/5/14 11:41 AM.
 */
enum LogLevel {
    VERBOSE(Log.VERBOSE),
    DEBUG(Log.DEBUG),
    INFO(Log.INFO),
    WARN(Log.WARN),
    ERROR(Log.ERROR),
    ASSERT(Log.ASSERT);

    private final int mLevel;

    private LogLevel(int level) {
        mLevel = level;
    }

    public boolean isValidLevel(int level) {
        return level > mLevel;
    }
}
