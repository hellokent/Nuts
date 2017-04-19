package io.demor.nuts.lib.eventbus;

import io.demor.nuts.lib.controller.AppInstance;
import io.demor.nuts.lib.controller.MethodInfoUtil;
import io.demor.nuts.lib.log.LogContext;
import io.demor.nuts.lib.module.PushObject;

public class LogBarrier extends BaseBarrier{

    private volatile boolean mRunning;
    private Thread mLogThread;

    public LogBarrier(final AppInstance appInstance) throws Exception {
        super(appInstance);
    }

    public void printLog() {
        if (mRunning) {
            return;
        }
        mRunning = true;
        mLogThread = new Thread(() -> {
            while (mRunning) {
                try {
                    PushObject pushObject = mQueue.take();
                    if (pushObject.mType != PushObject.TYPE_LOG) {
                        continue;
                    }
                    LogContext logContext = MethodInfoUtil.GSON.fromJson(pushObject.mData.toString(), LogContext.class);
                    System.out.println(String.format("instance: %s:%b\n%s:%s %s.%s(%s)\t%s",
                            mAppInstance.mHost, mAppInstance.mHttpPort,
                            logContext.mThreadName,
                            logContext.mThreadId,
                            logContext.mClass,
                            logContext.mMethod,
                            logContext.mLineNumber,
                            logContext.mMsg
                            ));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        mLogThread.start();
    }

    public void stop() {
        mRunning = false;
        mLogThread.interrupt();
    }

}
