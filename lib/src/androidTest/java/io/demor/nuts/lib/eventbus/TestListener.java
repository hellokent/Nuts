package io.demor.nuts.lib.eventbus;

import io.demor.nuts.lib.annotation.eventbus.DeepClone;

interface TestListener {
    void onSuccess();

    void onFailed();

    @DeepClone
    void onDeepClone(int hashCode, Exception e);

    void onDirect(int hashCode, Exception e);
}
