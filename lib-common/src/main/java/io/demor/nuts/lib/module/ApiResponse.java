package io.demor.nuts.lib.module;

public class ApiResponse extends BaseResponse {

    public Object data;

    public static ApiResponse ofSuccess(Object data) {
        final ApiResponse resp = new ApiResponse();
        resp.data = data;
        return resp;
    }

    public static ApiResponse ofFailed(String message) {
        final ApiResponse resp = new ApiResponse();
        resp.code = -1;
        resp.message = message;
        return resp;
    }
}
