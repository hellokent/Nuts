package io.demor.nuts.lib.net;

import java.io.Serializable;
import java.util.Map;

public interface IResponse extends Serializable {

    int BAD_NETWORK = -1024;

    void setErrorCode(int errorCode);

    void setStatusCode(int statusCode);

    void setHeader(Map<String, String> header);
}
