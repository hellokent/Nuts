package io.demor.server.template;

import io.demor.nuts.common.server.AbstractTemplate;
import io.demor.server.ServerManager;

import java.util.Map;

public class WsTemplate extends AbstractTemplate {

    @Override
    public Map<String, Object> getParam(Map<String, String> param) {
        mParam.put("url", "ws://" + ServerManager.getIpAddress() + ":" + ServerManager.getWebSocketPort());
        return super.getParam(param);
    }
}
