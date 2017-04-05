package io.demor.nuts.lib;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;

import io.demor.nuts.lib.eventbus.EventBus;
import io.demor.nuts.lib.task.SafeTask;

public interface Globals {
    Handler UI_HANDLER = new Handler(Looper.getMainLooper());
    Executor UI_EXECUTOR = new Executor() {
        @Override
        public void execute(Runnable command) {
            UI_HANDLER.post(command);
        }
    };
    Executor BG_EXECUTOR = new Executor() {
        @Override
        public void execute(Runnable command) {
            SafeTask.THREAD_POOL_EXECUTOR.execute(command);
        }
    };
    EventBus BUS = new EventBus(BG_EXECUTOR, UI_EXECUTOR);
}
