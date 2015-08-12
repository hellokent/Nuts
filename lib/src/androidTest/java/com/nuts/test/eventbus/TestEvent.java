package com.nuts.test.eventbus;

import com.nuts.lib.eventbus.BaseEvent;

public class TestEvent extends BaseEvent<Integer> {

    public TestEvent(final Integer data) {
        super(data);
    }
}
