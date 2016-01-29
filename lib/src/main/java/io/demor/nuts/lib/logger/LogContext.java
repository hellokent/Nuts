package io.demor.nuts.lib.logger;

import io.demor.nuts.lib.annotation.log.LogFormatKeyword;

import java.util.Calendar;

public class LogContext {
    @LogFormatKeyword("msg")
    public String mMsg;

    @LogFormatKeyword("time")
    public String mTime;

    @LogFormatKeyword("tag")
    public String mTag;

    @LogFormatKeyword("threadName")
    public String mThreadName;

    @LogFormatKeyword("threadId")
    public long mThreadId;

    @LogFormatKeyword("class")
    public String mClass;

    @LogFormatKeyword("method")
    public String mMethod;

    public int mLevel;

    Thread mCurrentThread;
    Calendar mCalendar;

    public LogContext() {
        mCurrentThread = Thread.currentThread();
        mThreadId = mCurrentThread.getId();
        mThreadName = mCurrentThread.getName();
        mCalendar = Calendar.getInstance();
    }
}
