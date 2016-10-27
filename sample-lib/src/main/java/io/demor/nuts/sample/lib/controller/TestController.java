package io.demor.nuts.sample.lib.controller;

import io.demor.nuts.lib.annotation.controller.CheckActivity;
import io.demor.nuts.lib.controller.Return;

public interface TestController {

    Return<String> run(int count);

    @CheckActivity
    Return<Void> runCheckActivity();

    Return<Void> runWithException();
}
