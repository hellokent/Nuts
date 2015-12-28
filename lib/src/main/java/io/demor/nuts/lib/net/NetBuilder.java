package io.demor.nuts.lib.net;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.squareup.okhttp.*;
import io.demor.nuts.lib.annotation.net.Param;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

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

    final String mLogTag;

    protected String mUrl;

    int mStatusCode = -1;

    HttpMethod mHttpMethod;

    NetBuilder(INet net, String url, Class<?> respClz, HttpMethod httpMethod, Method method,
               Object[] args) {
        mUrl = url;
        mNet = net;
        mRespClz = respClz;
        mHttpClient = new OkHttpClient();
        mHttpClient.setConnectTimeout(net.getConnectionTimeout(), TimeUnit.SECONDS);
        mHttpClient.setWriteTimeout(net.getWriteTimeout(), TimeUnit.SECONDS);
        mHttpClient.setReadTimeout(net.getReadTimeout(), TimeUnit.SECONDS);
        mMethod = method;
        mArgs = args;
        mHttpMethod = httpMethod;
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

    public NetResult execute() {
        try {
            return mHttpMethod.execute(this);
        } catch (IllegalArgumentException e) {
            return ofFailed(e);
        }
    }

    String getLog4Param() {
        return Joiner.on(",")
                .withKeyValueSeparator("=")
                .useForNull("")
                .join(mParams);
    }

    String getURLParam() {
        return Joiner.on("&")
                .withKeyValueSeparator("=")
                .useForNull("")
                .join(getEncodedParam());
    }


    NetResult load(Request request) {
        ResponseBody body = null;
        try {
            final Response response = mHttpClient.newCall(request)
                    .execute();
            mStatusCode = response.code();
            body = response.body();
            return ofSuccess(response, body.bytes());
        } catch (Exception e) {
            return ofFailed(e);
        } finally {
            if (body != null) {
                try {
                    body.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    NetResult ofSuccess(Response response, byte[] data) {
        final NetResult result = new NetResult();
        result.mIsSuccess = true;
        result.mResult = data;
        HashMap<String, String> headerMap = Maps.newHashMap();
        for (String name : response.headers().names()) {
            headerMap.put(name, response.header(name));
        }
        result.mHeader.putAll(headerMap);
        result.mStatusCode = response.code();

        result.mIResponse = mNet.createResponse(mRespClz, data);
        result.mIResponse.setStatusCode(response.code());
        result.mIResponse.setHeader(headerMap);
        return result;
    }

    NetResult ofFailed(Exception e) {
        final NetResult result = new NetResult();
        result.mIsSuccess = false;
        result.mException = e;
        result.mStatusCode = mStatusCode;

        result.mIResponse = mNet.createInvalidateResponse(mRespClz);
        result.mIResponse.setErrorCode(IResponse.BAD_NETWORK);
        result.mIResponse.setStatusCode(mStatusCode);
        return result;
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

    public void initParam() {
        mUrl = mNet.onCreateUrl(mUrl, mMethod, mArgs);
        mNet.onCreateParams(mParams, mHeaders, mMethod, mArgs);
    }

    enum HttpMethod implements INetExecutor {
        GET {
            @Override
            public NetResult execute(final NetBuilder builder) {
                return builder.load(new Request.Builder().url(builder.mUrl + "?" + builder.getURLParam())
                        .headers(builder.getHeaders())
                        .get()
                        .build());
            }
        },
        POST {
            @Override
            public NetResult execute(final NetBuilder builder) {
                return builder.load(new Request.Builder().url(builder.mUrl)
                        .headers(builder.getHeaders())
                        .post(RequestBody.create(CONTENT_TYPE, builder.getURLParam()))
                        .build());
            }
        },
        MULTIPART {
            @Override
            public NetResult execute(final NetBuilder builder) {
                final MultipartBuilder multipartBuilder = new MultipartBuilder().type(MultipartBuilder.FORM);

                for (Map.Entry<String, String> entry : builder.mParams.entrySet()) {
                    multipartBuilder.addFormDataPart(entry.getKey(), entry.getValue());
                }

                for (Map.Entry<String, UploadFileRequest> entry : builder.mFiles.entrySet()) {
                    if (entry.getValue() == null) {
                        continue;
                    }
                    final UploadFileRequest uploadFileRequest = entry.getValue();
                    final File file = uploadFileRequest.mFile;
                    final MediaType type = MediaType.parse(uploadFileRequest.mType);
                    if (uploadFileRequest.mListener == null) {
                        multipartBuilder.addFormDataPart(entry.getKey(), file.getPath(), RequestBody.create(type,
                                file));
                    } else {
                        multipartBuilder.addFormDataPart(entry.getKey(), file.getPath(), new CountingFileRequestBody
                                (type, file, uploadFileRequest.mListener));
                    }
                }

                final Request request = new Request.Builder().url(builder.mUrl)
                        .headers(builder.getHeaders())
                        .post(multipartBuilder.build())
                        .build();

                return builder.load(request);
            }
        },

        PUT {
            @Override
            public NetResult execute(final NetBuilder builder) {
                return builder.load(new Request.Builder().url(builder.mUrl)
                        .headers(builder.getHeaders())
                        .put(RequestBody.create(CONTENT_TYPE, builder.getURLParam()))
                        .build());
            }
        },

        DELETE {
            @Override
            public NetResult execute(final NetBuilder builder) {
                return builder.load(new Request.Builder().url(builder.mUrl + "?" + builder.getURLParam())
                        .headers(builder.getHeaders())
                        .delete()
                        .build());
            }
        },

        PATCH {
            @Override
            public NetResult execute(final NetBuilder builder) {
                return builder.load(new Request.Builder().url(builder.mUrl)
                        .headers(builder.getHeaders())
                        .patch(RequestBody.create(CONTENT_TYPE, builder.getURLParam()))
                        .build());
            }
        }
    }

    interface INetExecutor {
        NetResult execute(NetBuilder builder);
    }

    static class UploadFileRequest{
        public File mFile;
        public ProgressListener mListener;
        public String mType;
    }

}
