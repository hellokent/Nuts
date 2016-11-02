package io.demor.nuts.lib.controller;

import com.squareup.okhttp.*;
import io.demor.nuts.lib.ReflectUtils;
import io.demor.nuts.lib.module.ControllerInvocationResponse;
import okio.Buffer;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;

import static io.demor.nuts.lib.controller.ControllerUtil.GSON;
import static io.demor.nuts.lib.controller.ControllerUtil.fromJson;

public final class ReturnImpl<T> extends Return<T> {

    private final String mHost;
    private final int mPort;
    private final OkHttpClient mClient;
    private final Object[] mArgs;

    public ReturnImpl(Method method, String host, int port, Object[] args) {
        super(null, method);
        mHost = host;
        mPort = port;
        mClient = new OkHttpClient();
        mArgs = args;
        mClient.interceptors().add(new Interceptor() {
            private final Charset UTF8 = Charset.forName("UTF-8");

            @Override
            public Response intercept(Chain chain) throws IOException {
                final Request request = chain.request();
                boolean compressed = false;

                StringBuilder curlCmd = new StringBuilder("curl");
                curlCmd.append(" -X ").append(request.method());

                Headers headers = request.headers();
                for (int i = 0, count = headers.size(); i < count; i++) {
                    String name = headers.name(i);
                    String value = headers.value(i);
                    if ("Accept-Encoding".equalsIgnoreCase(name) && "gzip".equalsIgnoreCase(value)) {
                        compressed = true;
                    }
                    curlCmd.append(" -H ").append("\"").append(name).append(": ").append(value).append("\"");
                }

                RequestBody requestBody = request.body();
                if (requestBody != null) {
                    Buffer buffer = new Buffer();
                    requestBody.writeTo(buffer);
                    Charset charset = UTF8;
                    MediaType contentType = requestBody.contentType();
                    if (contentType != null) {
                        charset = contentType.charset(UTF8);
                    }
                    // try to keep to a single line and use a subshell to preserve any line breaks
                    curlCmd.append(" --data $'").append(buffer.readString(charset).replace("\n", "\\n")).append("'");
                }

                curlCmd.append((compressed) ? " --compressed " : " ").append(request.url());

                System.out.println(curlCmd.toString());

                return chain.proceed(request);
            }
        });
    }

    @Override
    public T sync() {
        try {
            String resp = mClient.newCall(new Request.Builder()
                    .url(String.format("http://%s:%d/api/controller/%s", mHost, mPort, mMethod.getDeclaringClass().getName()))
                    .header("content-type", "application/json")
                    .post(RequestBody.create(MediaType.parse("json"), ControllerUtil.generateControllerMethod(mMethod, mArgs)))
                    .build())
                    .execute()
                    .body()
                    .string();
            ControllerInvocationResponse response = GSON.fromJson(resp, ControllerInvocationResponse.class);
            if (response.code == 0) {
                return (T) fromJson(response.mData, (Class<?>) ReflectUtils.getGenericType(mMethod.getGenericReturnType()));
            } else {
                throw new Error("bad response:" + response.message);
            }
        } catch (IOException e) {
            throw new Error("bad network", e);
        }
    }

    @Override
    public void asyncUI(ControllerCallback<T> callback) {
        throw new Error("No ui thread in PC mode");
    }
}
