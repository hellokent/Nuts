package io.demor.nuts.lib.net;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.annotations.Expose;

public class GsonDeserializeExclusionStrategy implements ExclusionStrategy {
    @Override
    public boolean shouldSkipField(final FieldAttributes f) {
        Expose e = f.getAnnotation(Expose.class);
        return e != null && !e.deserialize();
    }

    @Override
    public boolean shouldSkipClass(final Class<?> clazz) {
        Expose e = clazz.getAnnotation(Expose.class);
        return e != null && e.deserialize();
    }
}
