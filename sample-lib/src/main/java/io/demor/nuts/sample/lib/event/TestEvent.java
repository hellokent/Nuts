package io.demor.nuts.sample.lib.event;

import io.demor.nuts.lib.eventbus.BaseEvent;

public class TestEvent extends BaseEvent<String> {

    public TestEvent(final String data) {
        super(data);
    }
}
