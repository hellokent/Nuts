package com.nuts.sample.controller;

import com.nuts.lib.controller.CheckActivity;
import com.nuts.lib.controller.Return;
import com.nuts.lib.controller.VoidReturn;

public interface TestController {

    Return<String> run(int count);

    @CheckActivity
    VoidReturn runCheckActivity();
}
