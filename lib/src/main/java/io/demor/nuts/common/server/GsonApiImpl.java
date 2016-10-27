package io.demor.nuts.common.server;

import com.google.gson.Gson;

import java.util.Map;

class GsonApiImpl implements IApiMethod {
    IApi mIApi;
    Gson mGson;

    public GsonApiImpl(final Gson gson, final IApi api) {
        mIApi = api;
        mGson = gson;
    }

    @Override
    public String invoke(final Map<String, String> parameterMap, byte[] body) {
        Object o = mIApi.call(parameterMap, new String(body));
        if (o instanceof String) {
            return o.toString();
        } else {
            return mGson.toJson(o);
        }
    }
}
