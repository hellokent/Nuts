package io.demor.nuts.lib.eventbus;

public class TestEvent extends BaseEvent<Integer> {

    public TestEvent(final Integer data) {
        super(data);
    }
}
