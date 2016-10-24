package io.demor.nuts.sample.lib.controller;

import io.demor.nuts.lib.annotation.controller.CheckActivity;
import io.demor.nuts.lib.controller.Return;
import io.demor.nuts.lib.controller.VoidReturn;

public interface TestController {

    Return<String> run(int count);

    @CheckActivity
    VoidReturn runCheckActivity();

    VoidReturn runWithException();
}
