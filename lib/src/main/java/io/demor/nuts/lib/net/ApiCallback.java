package io.demor.nuts.lib.net;

import java.util.Map;

public class ApiCallback {
    public NetResult handle(ApiProcess process, String url, Map<String, ?> param, Map<String, String> header, String
            method) {
        return process.execute();
    }
}
