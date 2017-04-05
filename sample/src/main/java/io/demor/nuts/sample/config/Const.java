package io.demor.nuts.sample.config;

import com.google.common.reflect.Reflection;

import junit.framework.TestListener;

import io.demor.nuts.lib.Globals;
import io.demor.nuts.lib.NutsApplication;
import io.demor.nuts.lib.controller.ControllerInvokeHandler;
import io.demor.nuts.lib.eventbus.ListenerBus;
import io.demor.nuts.lib.jumper.JumperInvokeHandler;
import io.demor.nuts.lib.storage.SharedPreferenceStorageEngine;
import io.demor.nuts.lib.storage.Storage;
import io.demor.nuts.sample.GlobalApplication;
import io.demor.nuts.sample.controller.impl.TestControllerImpl;
import io.demor.nuts.sample.lib.controller.TestController;
import io.demor.nuts.sample.lib.event.SimpleListener;
import io.demor.nuts.sample.lib.module.SimpleObject;

public interface Const extends Globals{
    TestController TEST_CONTROLLER = Reflection.newProxy(TestController.class, new ControllerInvokeHandler<>(new TestControllerImpl()));

    ListenerBus LISTENER_BUS = new ListenerBus(BG_EXECUTOR, UI_EXECUTOR, NutsApplication.sApiServer);

    TestListener TEST_LISTENER = LISTENER_BUS.provide(TestListener.class);

    Storage<SimpleObject> SIMPLE_OBJECT_STORAGE = new Storage.Builder<SimpleObject>()
            .setClass(SimpleObject.class)
            .setStorageEngine(new SharedPreferenceStorageEngine(GlobalApplication.getGlobalContext()))
            .build();

    SimpleListener SIMPLE_LISTENER = LISTENER_BUS.provide(SimpleListener.class);

    Jumper JUMPER = Reflection.newProxy(Jumper.class, new JumperInvokeHandler(GlobalApplication.getGlobalContext()));
}
