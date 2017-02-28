package io.demor.server.api;

import android.graphics.drawable.StateListDrawable;
import io.demor.nuts.lib.server.IApi;
import io.demor.server.model.drawable.StateListResponse;
import org.joor.Reflect;

import java.util.Map;

public class StateListDrawableInfoApi implements IApi {

    String mName;

    StateListDrawable mDrawable;
    int[][] mStateInfo;

    public StateListDrawableInfoApi(final String name, final StateListDrawable drawable) {
        mName = name;
        mDrawable = drawable;

        mStateInfo = Reflect.on(mDrawable)
                .field("mStateListState")
                .field("mStateSets")
                .get();
    }

    @Override
    public String name() {
        return "StateListDrawable/" + mName;
    }

    @Override
    public Object call(final Map<String, String> param, String body) {
        return new StateListResponse(mStateInfo, mName);
    }
}
