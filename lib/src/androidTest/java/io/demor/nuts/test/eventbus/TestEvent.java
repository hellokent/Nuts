package io.demor.nuts.test.eventbus;

import io.demor.nuts.lib.eventbus.BaseEvent;

public class TestEvent extends BaseEvent<Integer> {

    public TestEvent(final Integer data) {
        super(data);
    }
}
