package com.nuts.sample.config;

import com.nuts.lib.jumper.ActivityInfo;
import com.nuts.lib.jumper.Extra;
import com.nuts.lib.jumper.IntentHandler;
import com.nuts.sample.ui.controller.CheckActivityControllerActivity;
import com.nuts.sample.ui.controller.ControllerActivity;
import com.nuts.sample.ui.controller.SimpleControllerActivity;
import com.nuts.sample.ui.jumper.JumperActivity;
import com.nuts.sample.ui.jumper.JumperSimpleActivity;

public interface Jumper {

    @ActivityInfo(clz = SimpleControllerActivity.class)
    IntentHandler simpleController();

    @ActivityInfo(clz = ControllerActivity.class)
    IntentHandler viewController();

    @ActivityInfo(clz = CheckActivityControllerActivity.class)
    IntentHandler checkActivityController();

    @ActivityInfo(clz = JumperActivity.class)
    IntentHandler viewJumper();

    @ActivityInfo(clz = JumperSimpleActivity.class)
    IntentHandler viewJumperSimple(@Extra(IntentNames.COUNT) int count);

    @ActivityInfo(action = "action")
    IntentHandler sendBroadcast();
}
