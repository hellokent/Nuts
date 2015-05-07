package com.nuts.lib.net;

import java.io.Serializable;

public interface IResponse extends Serializable {

    int BAD_NETWORK = -1024;

    void setErrorCode(int errorCode);
}
