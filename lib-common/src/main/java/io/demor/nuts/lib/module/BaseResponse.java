package io.demor.nuts.lib.module;

public class BaseResponse {
    public static final BaseResponse API_NOT_FOUND = new BaseResponse(1, "api找不到");
    public int code;
    public String message;

    public BaseResponse() {
        code = 0;
        message = "success";
    }

    public BaseResponse(int code, String message) {
        this.message = message;
        this.code = code;
    }

    public BaseResponse ofFailed(String msg) {
        code = -1;
        message = msg;
        return this;
    }
}
