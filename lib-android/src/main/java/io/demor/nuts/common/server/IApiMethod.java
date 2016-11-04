package io.demor.nuts.common.server;

import java.util.Map;

public interface IApiMethod {
    String invoke(Map<String, String> parameterMap, byte[] body);
}
