package com.nuts.sample.config;

import com.nuts.lib.jumper.ActivityInfo;
import com.nuts.lib.jumper.IntentHandler;
import com.nuts.sample.ui.controller.TestControllerActivity;

public interface Jumper {

    @ActivityInfo(clz = TestControllerActivity.class)
    IntentHandler testController();

}
