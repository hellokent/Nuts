package com.nuts.test.eventbus;

import com.nuts.lib.eventbus.BaseEvent;

public class TestSourceThreadEvent extends BaseEvent<Void> {

    public TestSourceThreadEvent() {
        super(null);
    }
}
