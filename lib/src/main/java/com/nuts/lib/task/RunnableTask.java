package com.nuts.lib.task;

/**
 * Created by demor on 10/9/14.
 */
public class RunnableTask extends SafeTask<Runnable, Void> {
    @Override
    protected Void doInBackground(final Runnable... params) {
        params[0].run();
        return null;
    }
}
