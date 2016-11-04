package io.demor.nuts.lib.net;

import com.google.gson.Gson;

public abstract class JsonNet extends INet {

    private Gson mGson;

    public JsonNet(Gson gson) {
        mGson = gson;
    }

    @Override
    public Object createResponse(Class clz, ApiResponse response) {
        if (response.isSuccess()) {
            try {
                return mGson.fromJson(new String(response.getResult()), clz);
            } catch (Throwable ignored) {
            }
        }
        try {
            return clz.newInstance();
        } catch (Exception e) {
            throw new Error(e);
        }
    }
}
