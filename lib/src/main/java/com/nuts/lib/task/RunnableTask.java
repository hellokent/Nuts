package com.nuts.lib.task;

public class RunnableTask extends SafeTask<Runnable, Void> {
    @Override
    protected Void doInBackground(final Runnable... params) {
        params[0].run();
        return null;
    }
}
