package io.demor.nuts.lib.eventbus;

import io.demor.nuts.lib.controller.AppInstance;
import io.demor.nuts.lib.module.PushObject;

public class ListenerBarrier extends BaseBarrier {

    public ListenerBarrier(final AppInstance appInstance) throws Exception {
        super(appInstance);
    }

    @Override
    protected void onReceiveData(final PushObject object) {

    }
}
