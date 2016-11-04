package io.demor.nuts.common.server;

import java.util.Map;

public interface IApi {

    String name();

    Object call(Map<String, String> param, String body);
}
