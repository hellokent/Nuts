package io.demor.nuts.lib.log;

import com.google.gson.annotations.Expose;
import io.demor.nuts.lib.annotation.log.LogFormatKeyword;

public class LogContext {
    @LogFormatKeyword("msg")
    public String mMsg;

    @LogFormatKeyword("time")
    public LogTime mTime;

    @LogFormatKeyword("tag")
    public String mTag;

    @LogFormatKeyword("threadName")
    public String mThreadName;

    @LogFormatKeyword("threadId")
    public long mThreadId;

    @LogFormatKeyword("class")
    public String mClass;

    @LogFormatKeyword("totalClass")
    public String mTotalClass;

    @LogFormatKeyword("method")
    public String mMethod;

    @LogFormatKeyword("line")
    public int mLineNumber;

    public int mLevel;

    @Expose(serialize = false, deserialize = false)
    public Thread mCurrentThread;

    public LogContext() {
        mCurrentThread = Thread.currentThread();
        mThreadId = mCurrentThread.getId();
        mThreadName = mCurrentThread.getName();
        mTime = new LogTime();
    }
}
