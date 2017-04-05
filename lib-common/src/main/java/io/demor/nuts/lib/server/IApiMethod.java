package io.demor.nuts.lib.server;

import java.util.Map;

public interface IApiMethod {
    String invoke(Map<String, String> parameterMap, byte[] body);
}
