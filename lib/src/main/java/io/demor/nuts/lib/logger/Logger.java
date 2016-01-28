package io.demor.nuts.lib.logger;

import android.util.Log;
import com.google.common.collect.Lists;
import io.demor.nuts.lib.ThreadSafeDateFormat;

import java.util.ArrayList;
import java.util.Date;

public class Logger {

    ArrayList<LogOutput> mLogOutputs = Lists.newArrayList();
    String mPath;

    private ThreadLocal<LogContext> mLocalLogContext = new ThreadLocal<>();
    private ThreadSafeDateFormat mTimeFormat = new ThreadSafeDateFormat("HH:mm:ss.SSS");
    private ThreadSafeDateFormat mDateFormat = new ThreadSafeDateFormat("yyyy-MM-dd");
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
        final LogContext context = getLogContext();
        context.mLevel = level;
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

        final Thread thread = Thread.currentThread();
        final StackTraceElement element = thread.getStackTrace()[5];

        context.mMethod = element.getMethodName();
        context.mClass = element.getClassName();
        context.mTime = mTimeFormat.format(new Date());
        context.mThreadId = thread.getId();
        context.mThreadName = thread.getName();
        context.mTag = mTag;
        return context;
    }

}
