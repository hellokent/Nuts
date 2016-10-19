package io.demor.server.template;

import io.demor.nuts.common.server.AbstractTemplate;
import io.demor.server.ServerManager;

import java.util.Map;

public class WidgetTemplate extends AbstractTemplate {

    @Override
    public Map<String, Object> getParam(Map<String, String> param) {
        mParam.put("screen", ServerManager.getHttpHost() + "/res/screen.jpg");
        mParam.put("host", ServerManager.getHttpHost());
        return mParam;
    }
}
