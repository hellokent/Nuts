package io.demor.nuts.lib.controller;

import com.squareup.okhttp.Headers;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.x5.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.Locale;

import io.demor.nuts.lib.ReflectUtils;
import io.demor.nuts.lib.module.ControllerInvocationResponse;
import okio.Buffer;

import static io.demor.nuts.lib.controller.ControllerUtil.GSON;
import static io.demor.nuts.lib.controller.ControllerUtil.fromJson;

public final class ReturnImpl<T> extends Return<T> {

    private String mHost;
    private int mPort;
    private OkHttpClient mClient;
    private Object[] mArgs;

    public ReturnImpl(final T data) {
        super(data);
    }

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
                    .url(String.format(Locale.getDefault(), "http://%s:%d/api/controller/%s", mHost, mPort, mMethod.getDeclaringClass().getName()))
                    .header("content-type", "application/json")
                    .post(RequestBody.create(MediaType.parse("json"), ControllerUtil.generateMethodInfo(mMethod, mArgs)))
                    .build())
                    .execute()
                    .body()
                    .string();
            ControllerInvocationResponse response = GSON.fromJson(resp, ControllerInvocationResponse.class);
            if (response.code == 0) {
                if (response.mData.startsWith("#")) {
                    ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(Base64.decode(response.mData.substring(1))));
                    throw (ExceptionWrapper) objectInputStream.readObject();
                } else {
                    return (T) fromJson(response.mData, (Class<?>) ReflectUtils.getGenericType(mMethod.getGenericReturnType()));
                }
            } else {
                throw new Error("bad response:" + response.message);
            }
        } catch (IOException e) {
            throw new Error("bad network", e);
        } catch (ClassNotFoundException e) {
            throw new Error("throw class not foune", e);
        }
    }

    @Override
    public void asyncUI(final ControllerCallback<T> callback) {
        new Thread() {
            @Override
            public void run() {
                final T data = sync();
                if (callback != null) {
                    callback.onResult(data);
                }
            }
        }.start();
    }
}
