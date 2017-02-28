package io.demor.server.model.drawable;

import com.google.common.collect.Lists;
import io.demor.nuts.lib.logger.Logger;
import io.demor.nuts.lib.logger.LoggerFactory;
import io.demor.nuts.lib.module.BaseResponse;
import io.demor.server.Constant;
import io.demor.server.WebDebug;

import java.util.ArrayList;

public class StateListResponse extends BaseResponse {

    private static final Logger LOGGER = LoggerFactory.getLogger(StateListResponse.class);
    public ArrayList<State> list = Lists.newArrayList();

    public StateListResponse(int[][] data, String name) {

        for (int i = 0, n = Constant.DRAWABLE_STATE_MAP.size(); i < n; ++i) {
            LOGGER.v("name=%s,", Constant.DRAWABLE_STATE_MAP.valueAt(i));
        }

        for (int i = 0; i < data.length; ++i) {
            if (data[i] == null) {
                continue;
            }
            final State state = new State();
            state.info = new StateListDrawableInfo(data[i]);
            state.url = WebDebug.getHttpHost() + "/res/" + name + ".jpg?index=" + i;
            list.add(state);
        }
    }

    class State {
        StateListDrawableInfo info;
        String url;
    }
}

