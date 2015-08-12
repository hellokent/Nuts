package com.nuts.lib.task;

public class RunnableTask extends SafeTask<Runnable, Void> {
    @Override
    protected Void doInBackground(final Runnable... params) {
        if (params == null || params.length == 0) {
            return null;
        }
        for (Runnable runnable : params) {
            runnable.run();
        }
        return null;
    }
}
