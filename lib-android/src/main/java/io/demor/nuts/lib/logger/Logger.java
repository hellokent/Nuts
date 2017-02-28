package io.demor.nuts.lib.logger;

import android.util.Log;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.util.ArrayList;

public class Logger {

    ArrayList<LogOutput> mLogOutputs = Lists.newArrayList();
    String mPath;
    String mTag;
    boolean mNeedTime = false;
    boolean mNeedThreadStack = false;
    private ThreadLocal<LogContext> mLocalLogContext = new ThreadLocal<>();

    Logger(String path, String tag) {
        mPath = path;
        mTag = tag;
    }

    public void v(String msg, Object... arg) {
        final int level = Log.VERBOSE;
        log(level, msg, arg);
    }

    public void d(String msg, Object... arg) {
        final int level = Log.DEBUG;
        log(level, msg, arg);
    }

    public void i(String msg, Object... arg) {
        final int level = Log.INFO;
        log(level, msg, arg);
    }

    public void w(String msg, Object... arg) {
        final int level = Log.WARN;
        log(level, msg, arg);
    }

    public void e(String msg, Object... arg) {
        final int level = Log.ERROR;
        log(level, msg, arg);
    }

    protected void configLoaded() {
        for (LogOutput output : mLogOutputs) {
            if (output.needCurrentTime()) {
                mNeedTime = true;
                break;
            }
        }

        for (LogOutput output : mLogOutputs) {
            if (output.needThreadStack()) {
                mNeedThreadStack = true;
                break;
            }
        }
    }

    protected void clear() {
        mLogOutputs.clear();
        mNeedTime = false;
        mNeedThreadStack = false;
    }

    protected void log(int level, String msg, Object... arg) {
        final LogContext context = getLogContext();
        context.mLevel = level;
        context.mMsg = arg == null || arg.length == 0 ? msg : String.format(msg, arg);
        for (LogOutput output : mLogOutputs) {
            output.append(context);
        }
    }

    private LogContext getLogContext() {
        final LogContext context;
        if (mLocalLogContext.get() == null) {
            context = new LogContext();
            mLocalLogContext.set(context);
        } else {
            context = mLocalLogContext.get();
        }

        context.mTag = mTag;

        if (mNeedThreadStack) {
            final StackTraceElement element = context.mCurrentThread.getStackTrace()[5];
            context.mMethod = element.getMethodName();
            context.mTotalClass = element.getClassName();
            context.mClass = context.mTotalClass.substring(context.mTotalClass.lastIndexOf(".") + 1);
            context.mLineNumber = element.getLineNumber();
        }
        if (mNeedTime) {
            context.mTime.updateTime(System.currentTimeMillis());
        }
        return context;
    }

    public void exception(final IOException e) {
        final int level = Log.ERROR;
        log(level, Throwables.getStackTraceAsString(e));
    }
}
