package io.demor.server.template;

import io.demor.nuts.lib.server.AbstractTemplate;
import io.demor.server.WebDebug;

import java.util.Map;

public class WidgetTemplate extends AbstractTemplate {

    @Override
    public Map<String, Object> getParam(Map<String, String> param) {
        mParam.put("screen", WebDebug.getHttpHost() + "/res/screen.jpg");
        mParam.put("host", WebDebug.getHttpHost());
        return mParam;
    }
}
