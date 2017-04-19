package io.demor.nuts.sample.lib.controller;

import io.demor.nuts.lib.annotation.controller.CheckActivity;
import io.demor.nuts.lib.controller.Return;
import io.demor.nuts.sample.lib.module.SimpleObject;

public interface TestController {

    Return<String> add(int count);

    Return<String> addAll(int... count);

    Return<Integer> get();

    @CheckActivity
    Return<Void> runCheckActivity();

    Return<Void> runWithException();

    Return<Void> sendEvent();

    Return<Void> callListenerInt(int count);

    Return<Void> callListenerString(String msg);

    int getCount();

    SimpleObject getStorage();

    Return<Void> doForLongTime();
}
