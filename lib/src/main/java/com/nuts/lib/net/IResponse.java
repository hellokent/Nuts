package com.nuts.lib.net;

import java.io.Serializable;
import java.util.HashMap;

public interface IResponse extends Serializable {

    int BAD_NETWORK = -1024;

    void setErrorCode(int errorCode);

    void setStatusCode(int statusCode);

    void setHeader(HashMap<String, String> header);
}
