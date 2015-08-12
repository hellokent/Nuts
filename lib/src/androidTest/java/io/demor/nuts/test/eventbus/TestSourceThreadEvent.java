package io.demor.nuts.test.eventbus;

import io.demor.nuts.lib.eventbus.BaseEvent;

public class TestSourceThreadEvent extends BaseEvent<Void> {

    public TestSourceThreadEvent() {
        super(null);
    }
}
