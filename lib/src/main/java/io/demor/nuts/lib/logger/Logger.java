package io.demor.nuts.lib.logger;

import android.util.Log;
import com.google.common.collect.Lists;

import java.util.ArrayList;

public class Logger {


    ArrayList<LogOutput> mLogOutputs = Lists.newArrayList();
    String mPath;
    private ThreadLocal<LogContext> mLocalLogContext = new ThreadLocal<>();
    private String mTag;

    Logger(String path, String tag) {
        mPath = path;
        mTag = tag;
    }

    public void v(String msg, Object... arg) {
        final int level = Log.VERBOSE;
        log(level, String.format(msg, arg));
    }

    public void d(String msg, Object... arg) {
        final int level = Log.DEBUG;
        log(level, String.format(msg, arg));
    }

    public void i(String msg, Object... arg) {
        final int level = Log.INFO;
        log(level, String.format(msg, arg));
    }

    public void w(String msg, Object... arg) {
        final int level = Log.WARN;
        log(level, String.format(msg, arg));
    }

    public void e(String msg, Object... arg) {
        final int level = Log.ERROR;
        log(level, String.format(msg, arg));
    }

    protected void log(int level, String content) {
        boolean needTime = false;
        for (LogOutput output : mLogOutputs) {
            if (output.needCurrentTime()) {
                needTime = true;
                break;
            }
        }

        boolean needThreadStack = false;
        for (LogOutput output : mLogOutputs) {
            if (output.needThreadStack()) {
                needThreadStack = true;
                break;
            }
        }
        final LogContext context = getLogContext(needTime, needThreadStack);
        context.mLevel = level;
        for (LogOutput output : mLogOutputs) {
            output.append(context);
        }
    }

    private LogContext getLogContext(boolean needTime, boolean needThreadStack) {
        final LogContext context;
        if (mLocalLogContext.get() == null) {
            context = new LogContext();
            mLocalLogContext.set(context);
        } else {
            context = mLocalLogContext.get();
        }

        context.mTag = mTag;

        if (needThreadStack) {
            final StackTraceElement element = context.mCurrentThread.getStackTrace()[5];
            context.mMethod = element.getMethodName();
            context.mClass = element.getClassName();
        }
        if (needTime) {
            context.mTime.updateTime(System.currentTimeMillis());
        }
        return context;
    }

}
