package io.demor.nuts.lib.server;

import java.util.Map;

public interface IApi {

    String name();

    Object call(Map<String, String> param, String body);
}
