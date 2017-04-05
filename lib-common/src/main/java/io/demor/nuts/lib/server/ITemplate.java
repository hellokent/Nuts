package io.demor.nuts.lib.server;

import java.util.Map;

public interface ITemplate {
    Map<String, Object> getParam(Map<String, String> param);
}
