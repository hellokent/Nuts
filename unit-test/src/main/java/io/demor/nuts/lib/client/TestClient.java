package io.demor.nuts.lib.client;

import android.os.Handler;
import android.os.HandlerThread;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.demor.nuts.lib.Storage;
import io.demor.nuts.lib.eventbus.BaseEvent;
import io.demor.nuts.lib.eventbus.EventBus;
import io.demor.nuts.lib.model.ControllerInfo;
import io.demor.nuts.lib.model.GsonObject;
import io.demor.nuts.lib.storage.FileEngine;

import java.io.File;
import java.io.IOException;

public class TestClient {

    public static final String INSTANCE_FOLDER = "/tmp/nuts-unit-test";

    //TODO GSON use too much
    public static final Gson GSON = new GsonBuilder()
            .create();

    public static final Storage<AppInstance> INSTANCE_STORAGE = new Storage.Builder<AppInstance>()
            .setStorageEngine(new FileEngine(new File(INSTANCE_FOLDER)))
            .build();

    public static final ApiServer API_SERVER = new ApiServer(TestClient.INSTANCE_STORAGE.get());
    public static final WebSocketServer EVENT_SERVER = new WebSocketServer();

    public static final Handler EVENT_BUS_HANDLER;

    static {
        final HandlerThread thread = new HandlerThread("event-bus-post-thread");
        thread.start();
        EVENT_BUS_HANDLER = new Handler(thread.getLooper());
    }

    public static void startServer() throws IOException {
        API_SERVER.start();
        EVENT_SERVER.start();
    }

    public static void stopServer() {
        EVENT_SERVER.stop();
        API_SERVER.stop();
    }

    //TODO add in
    public static void addControllerImpl(Class<?> impl) {
        final AppInstance instance = TestClient.INSTANCE_STORAGE.get();
        for (Class<?> cont : impl.getInterfaces()) {
            final String name = cont.getName();
            if (name.startsWith("java") ||
                    name.startsWith("android")) {
                continue;
            }
            final ControllerInfo info = new ControllerInfo();
            info.mClassName = name;
            instance.mControllers.add(info);
        }
        TestClient.INSTANCE_STORAGE.save(instance);
    }

    public static void addEventBus(final EventBus bus) {
        bus.setPostListener(new EventBus.IPostEvent() {
            @Override
            public void onPostEvent(final BaseEvent o) {
                EVENT_BUS_HANDLER.post(new Runnable() {
                    @Override
                    public void run() {
                        if (EVENT_SERVER.isAlive()) {
                            EVENT_SERVER.sendText(TestClient.GSON.toJson(new GsonObject(o)));
                        }
                    }
                });
            }
        });
    }

    public static void addStorage(Storage<?> storage) {
        // TODO
    }

}
