package io.demor.server.template;

import io.demor.nuts.common.server.AbstractTemplate;
import io.demor.server.ServerManager;

import java.util.Map;

public class SnifferTemplate extends AbstractTemplate {

    @Override
    public Map<String, Object> getParam(Map<String, String> param) {
        mParam.put("host", ServerManager.getIpAddress() + ":" + ServerManager.getWebSocketPort());
        return mParam;
    }
}
