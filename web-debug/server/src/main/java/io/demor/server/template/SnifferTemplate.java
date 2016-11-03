package io.demor.server.template;

import io.demor.nuts.common.server.AbstractTemplate;
import io.demor.server.WebDebug;

import java.util.Map;

public class SnifferTemplate extends AbstractTemplate {

    @Override
    public Map<String, Object> getParam(Map<String, String> param) {
        mParam.put("host", WebDebug.getIpAddress() + ":" + WebDebug.getWebSocketPort());
        return mParam;
    }
}
