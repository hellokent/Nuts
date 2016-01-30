package io.demor.nuts.lib.net;

public class ApiCallback {
    public ApiResponse handle(ApiRequest request) {
        return request.execute();
    }
}
