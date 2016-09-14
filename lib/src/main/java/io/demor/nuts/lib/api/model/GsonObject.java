package io.demor.nuts.lib.api.model;

import io.demor.nuts.lib.api.TestClient;

public class GsonObject {
    public String mClass;
    public String mGson;

    public GsonObject(Object o) {
        if (o == null) {
            mClass = "null";
            mGson = "null";
        } else {
            mClass = o.getClass().getName();
            mGson = TestClient.GSON.toJson(o);
        }
    }

    public Object toObj() {
        try {
            return TestClient.GSON.fromJson(mGson, Class.forName(mClass));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
