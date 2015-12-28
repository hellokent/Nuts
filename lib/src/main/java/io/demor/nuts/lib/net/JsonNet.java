package io.demor.nuts.lib.net;

import com.google.gson.Gson;

public abstract class JsonNet extends INet {

    private Gson mGson;

    public JsonNet(Gson gson) {
        mGson = gson;
    }

    @Override
    protected IResponse createResponse(Class clz, byte[] data) {
        return (IResponse) mGson.fromJson(new String(data), clz);
    }
}
