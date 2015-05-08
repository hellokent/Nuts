package com.nuts.sample.config;

import com.nuts.lib.jumper.ActivityInfo;
import com.nuts.lib.jumper.IntentHandler;
import com.nuts.sample.ui.controller.ControllerActivity;
import com.nuts.sample.ui.controller.SimpleControllerActivity;

public interface Jumper {

    @ActivityInfo(clz = SimpleControllerActivity.class)
    IntentHandler simpleController();

    @ActivityInfo(clz = ControllerActivity.class)
    IntentHandler viewController();

}
