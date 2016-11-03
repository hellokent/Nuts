package io.demor.nuts.sample.config;

import io.demor.nuts.lib.annotation.jumper.ActivityInfo;
import io.demor.nuts.lib.annotation.jumper.Extra;
import io.demor.nuts.lib.jumper.IntentHandler;
import io.demor.nuts.sample.ui.NetWatcherActivity;
import io.demor.nuts.sample.ui.controller.ControllerActivity;
import io.demor.nuts.sample.ui.controller.ControllerCheckActivityActivity;
import io.demor.nuts.sample.ui.controller.ControllerLifeCircleActivity;
import io.demor.nuts.sample.ui.controller.ControllerSimpleActivity;
import io.demor.nuts.sample.ui.jumper.JumperActivity;
import io.demor.nuts.sample.ui.jumper.JumperSimpleActivity;

public interface Jumper {

    @ActivityInfo(clz = ControllerSimpleActivity.class)
    IntentHandler simpleController();

    @ActivityInfo(clz = ControllerActivity.class)
    IntentHandler viewController();

    @ActivityInfo(clz = ControllerCheckActivityActivity.class)
    IntentHandler checkActivityController();

    @ActivityInfo(clz = JumperActivity.class)
    IntentHandler viewJumper();

    @ActivityInfo(clz = JumperSimpleActivity.class)
    IntentHandler viewJumperSimple(@Extra(IntentNames.COUNT) int count);

    @ActivityInfo(clz = ControllerLifeCircleActivity.class)
    IntentHandler viewControllerLifeCircle();

    @ActivityInfo(action = "action")
    IntentHandler sendBroadcast();

    @ActivityInfo(clz = NetWatcherActivity.class)
    IntentHandler viewNetWatcher();
}
