package io.demor.nuts.lib.api.model;

import com.google.common.collect.Lists;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import io.demor.nuts.lib.client.TestClient;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

public class AppInstance {
    public String mName;
    public int mPort;
    public String mPackageName;

    public List<ControllerInfo> mControllers = Lists.newArrayList();
    //TODO eventInfo storageInfo

    private transient OkHttpClient mHttpClient = new OkHttpClient();

    public String callController(final String className, final Method method, final Object[] args) throws IOException {

        final GsonObject[] gsonArgs = new GsonObject[args.length];
        for (int i = 0; i < args.length; ++i) {
            gsonArgs[i] = new GsonObject(args[i]);
        }

        return mHttpClient.newCall(new Request.Builder()
                .url(String.format("http://localhost:%s/c/%s/%s", mPort, className, method.getName()))
                .post(RequestBody.create(MediaType.parse("application/json"),
                        TestClient.GSON.toJson(gsonArgs)))
                .build())
                .execute().body().string();
    }

    public Object invokeController(final String controllerName, final String method, final GsonObject[] args) {
        for (ControllerInfo info : mControllers) {
            if (controllerName.equals(info.mClassName)) {
                try {
                    return info.callFromJson(method, args);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
        return null;
    }
}
