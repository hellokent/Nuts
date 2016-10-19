package io.demor.server.model.drawable;

import com.google.common.collect.Lists;
import io.demor.server.Constant;

import java.util.ArrayList;

public class StateListDrawableInfo {

    public ArrayList<State> list = Lists.newArrayList();

    public StateListDrawableInfo(int[] data) {
        for (int info : data) {
            final State state = new State();
            state.status = info > 0;
            state.name = Constant.DRAWABLE_STATE_MAP.get(info > 0 ? info : -info);
            list.add(state);
        }
    }

    public class State {
        public String name;
        public boolean status;
    }
}
