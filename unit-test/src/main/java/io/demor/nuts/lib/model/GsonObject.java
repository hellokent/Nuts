package io.demor.nuts.lib.model;

import static io.demor.nuts.lib.client.TestClient.GSON;

public class GsonObject {
    public String mClass;
    public String mGson;

    public GsonObject(Object o) {
        if (o == null) {
            mClass = "null";
            mGson = "null";
        } else {
            mClass = o.getClass().getName();
            mGson = GSON.toJson(o);
        }
    }

    public Object toObj() {
        try {
            return GSON.fromJson(mGson, Class.forName(mClass));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
