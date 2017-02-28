package io.demor.server.template;

import io.demor.nuts.lib.server.AbstractTemplate;
import io.demor.server.WebDebug;

import java.util.Map;

public class WsTemplate extends AbstractTemplate {

    @Override
    public Map<String, Object> getParam(Map<String, String> param) {
        mParam.put("url", "ws://" + WebDebug.getIpAddress() + ":" + WebDebug.getWebSocketPort());
        return super.getParam(param);
    }
}
