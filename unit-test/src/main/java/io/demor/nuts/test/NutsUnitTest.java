package io.demor.nuts.test;

import com.google.gson.Gson;
import io.demor.nuts.lib.Storage;
import io.demor.nuts.lib.eventbus.BaseEvent;
import io.demor.nuts.lib.eventbus.EventBus;
import io.demor.nuts.lib.storage.FileEngine;
import io.demor.nuts.test.model.AppInstance;
import io.demor.nuts.test.model.ControllerInfo;

import java.io.IOException;

public class NutsUnitTest {

    public static final Gson GSON = new Gson();

    public static final Storage<AppInstance> INSTANCE_STORAGE = new Storage.Builder<AppInstance>()
            .setStorageEngine(new FileEngine("/tmp/nuts-unit-test"))
            .build();

    public static final ApiServer API_SERVER = new ApiServer();
    public static final WebSocketServer EVENT_SERVER = new WebSocketServer();

    public static void startServer() throws IOException {
        API_SERVER.start();
        EVENT_SERVER.start();
    }

    public static void stopServer() {
        EVENT_SERVER.stop();
        API_SERVER.stop();
    }


    public static void addControllerImpl(Class<?> impl) {
        AppInstance instance = NutsUnitTest.INSTANCE_STORAGE.get();
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
        NutsUnitTest.INSTANCE_STORAGE.save(instance);
    }

    public static void addEventBus(EventBus bus) {
        // TODO
        bus.setPostListener(new EventBus.IPostEvent() {
            @Override
            public void onPostEvent(BaseEvent o) {
                if (!EVENT_SERVER.isAlive()) {
                    return;
                }
                EVENT_SERVER.
            }
        });
    }

    public static void addStorage(Storage<?> storage) {
        // TODO
    }

}
