package com.nuts.lib.net;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Joiner;
import com.google.gson.Gson;
import com.nuts.lib.annotation.net.Param;
import com.nuts.lib.log.L;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import static com.nuts.lib.net.NetResult.ofFailed;
import static com.nuts.lib.net.NetResult.ofSuccess;

class NetBuilder {

    private static final MediaType CONTENT_TYPE = MediaType.parse("application/x-www-form-urlencoded");

    protected final TreeMap<String, String> mParams = new TreeMap<>();

    protected final TreeMap<String, String> mHeaders = new TreeMap<>();

    protected final TreeMap<String, UploadFileRequest> mFiles = new TreeMap<>();

    final Class<?> mRespClz;

    final OkHttpClient mHttpClient;

    final INet mNet;

    final Method mMethod;

    final Object[] mArgs;

    final Gson mGson;

    final String mLogTag;

    protected String mUrl;

    int mStatusCode = -1;

    NetBuilder(final Gson gson, INet net, String url, Class<?> respClz, Method method, Object[] args) {
        mUrl = url;
        mNet = net;
        mRespClz = respClz;
        mHttpClient = new OkHttpClient();
        mHttpClient.setConnectTimeout(net.getConnectionTimeout(), TimeUnit.SECONDS);
        mHttpClient.setWriteTimeout(net.getWriteTimeout(), TimeUnit.SECONDS);
        mHttpClient.setReadTimeout(net.getReadTimeout(), TimeUnit.SECONDS);
        mMethod = method;
        mArgs = args;
        mGson = gson;

        mLogTag = mNet.getLogTag(mUrl, mMethod, mArgs);
    }

    public void addParam(Param param, Object object) {
        if (object == null || param == null) {
            return;
        }

        final String key = param.value();

        if (object instanceof File) {
            final UploadFileRequest uploadFileRequest = mFiles.containsKey(key) ?
                    mFiles.get(key) : new UploadFileRequest();
            uploadFileRequest.mFile = (File) object;
            uploadFileRequest.mType = param.mediaType();
            mFiles.put(key, uploadFileRequest);
        } else if (object instanceof ProgressListener) {
            final UploadFileRequest uploadFileRequest = mFiles.containsKey(key) ? mFiles.get(key) : new UploadFileRequest();
            uploadFileRequest.mListener = (ProgressListener) object;
            mFiles.put(key, uploadFileRequest);
        } else {
            mParams.put(key, object.toString());
        }
    }

    public void setUrl(final String url) {
        mUrl = url;
    }

    public NetResult get() {
        initParam();
        L.i(">>> GET(%s):%s", mLogTag, Joiner.on(",")
                .withKeyValueSeparator("=")
                .join(mParams));
        try {
            final String respStr = executeNet(new Request.Builder().url(mUrl + "?" +
                    Joiner.on("&")
                            .withKeyValueSeparator("=")
                            .useForNull("")
                            .join(getEncodedParam()))
                    .headers(getHeaders())
                    .get()
                    .build());
            L.i("<<< GET(%s):%s", mLogTag, respStr);
            return ofSuccess((IResponse) mGson.fromJson(respStr, mRespClz));
        } catch (Throwable e) {
            L.e("!!! ERROR GET(%s), %s", mLogTag, e.getMessage());
            L.exception(e);
            return ofFailed(createInvalidResponse(), mStatusCode);
        }
    }

    public NetResult post() {
        initParam();
        L.i(">>> POST(%s):%s", mLogTag, Joiner.on(",")
                .withKeyValueSeparator("=")
                .join(mParams));
        try {
            final String respStr = executeNet(new Request.Builder().url(mUrl)
                    .headers(getHeaders())
                    .post(RequestBody.create(CONTENT_TYPE, Joiner.on("&")
                            .withKeyValueSeparator("=")
                            .useForNull("")
                            .join(mParams)))
                    .build());
            L.i("<<< POST(%s):%s", mLogTag, respStr);
            return ofSuccess((IResponse) mGson.fromJson(respStr, mRespClz));
        } catch (Exception e) {
            L.e("!!! ERROR POST(%s), %s", mLogTag, e.getMessage());
            L.exception(e);
            return ofFailed(createInvalidResponse(), mStatusCode);
        }
    }

    public NetResult multipart() {
        initParam();
        L.i(">>> MULTIPART(%s):%s;FILE:%s", mLogTag,
                Joiner.on(",")
                        .withKeyValueSeparator("=")
                        .join(mParams),
                Joiner.on(",")
                        .withKeyValueSeparator("=")
                        .join(mFiles));
        try {

            final MultipartBuilder builder = new MultipartBuilder().type(MultipartBuilder.FORM);

            for (Map.Entry<String, String> entry : mParams.entrySet()) {
                builder.addFormDataPart(entry.getKey(), entry.getValue());
            }

            for (Map.Entry<String, UploadFileRequest> entry : mFiles.entrySet()) {
                final UploadFileRequest uploadFileRequest = entry.getValue();
                final File file = uploadFileRequest.mFile;
                final MediaType type = MediaType.parse(uploadFileRequest.mType);
                if (uploadFileRequest.mListener == null) {
                    builder.addFormDataPart(entry.getKey(), file.getPath(), RequestBody.create(type, file));
                } else {
                    builder.addFormDataPart(entry.getKey(), file.getPath(),
                            new CountingFileRequestBody(type, file, uploadFileRequest.mListener));
                }
            }

            final Request request = new Request.Builder().url(mUrl)
                    .headers(getHeaders())
                    .post(builder.build())
                    .build();

            final String respStr = executeNet(request);
            L.i("<<< MULTIPART(%s):%s", mLogTag, respStr);
            return ofSuccess((IResponse) mGson.fromJson(respStr, mRespClz));
        } catch (Exception e) {
            L.e("!!! ERROR MULTIPART(%s), %s", mLogTag, e.getMessage());
            L.exception(e);
            return ofFailed(createInvalidResponse(), mStatusCode);
        }
    }

    String executeNet(Request request) throws IOException {
        final Response response = mHttpClient.newCall(request)
                .execute();
        mStatusCode = response.code();
        return response.body()
                .string();
    }

    Map<String, String> getEncodedParam() {
        final HashMap<String, String> result = new HashMap<>();
        for (Map.Entry<String, String> entry : mParams.entrySet()) {
            try {
                result.put(entry.getKey(), URLEncoder.encode(entry.getValue(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private Headers getHeaders() {
        final Headers.Builder builder = new Headers.Builder();
        for (Map.Entry<String, String> entry : mHeaders.entrySet()) {
            builder.add(entry.getKey(), entry.getValue());
        }
        return builder.build();
    }

    IResponse createInvalidResponse() {
        try {
            IResponse result = (IResponse) mRespClz.newInstance();
            result.setErrorCode(IResponse.BAD_NETWORK);
            return result;
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    private void initParam() {
        mUrl = mNet.onCreateUrl(mUrl, mMethod, mArgs);
        mNet.onCreateParams(mParams, mHeaders, mMethod, mArgs);
    }

    static class UploadFileRequest{
        public File mFile;
        public ProgressListener mListener;
        public String mType;
    }
}
