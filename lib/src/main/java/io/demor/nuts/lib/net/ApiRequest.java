package io.demor.nuts.lib.net;

import com.google.common.base.Joiner;
import com.squareup.okhttp.Headers;
import io.demor.nuts.lib.annotation.net.Param;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public final class ApiRequest {
    protected String mUrl;
    protected HttpMethod mHttpMethod;
    protected Map<String, String> mParams = new TreeMap<>();
    protected Map<String, String> mHeaders = new TreeMap<>();
    protected TreeMap<String, UploadFileRequest> mFiles = new TreeMap<>();
    private INet mNet;

    public ApiRequest(INet net) {
        mNet = net;
    }

    public static String joinMap(String separator, String keyValueSeparator, Map map) {
        return Joiner.on(separator).withKeyValueSeparator(keyValueSeparator).join(map);
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public HttpMethod getHttpMethod() {
        return mHttpMethod;
    }

    public void setHttpMethod(HttpMethod httpMethod) {
        mHttpMethod = httpMethod;
    }

    public Map<String, String> getParams() {
        return mParams;
    }

    public void setParams(Map<String, String> params) {
        mParams = params;
    }

    public Map<String, String> getHeaders() {
        return mHeaders;
    }

    public void setHeaders(Map<String, String> headers) {
        mHeaders = headers;
    }

    public TreeMap<String, UploadFileRequest> getFiles() {
        return mFiles;
    }

    public void setFiles(TreeMap<String, UploadFileRequest> files) {
        mFiles = files;
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


    String getURLParam() {
        return Joiner.on("&")
                .withKeyValueSeparator("=")
                .useForNull("")
                .join(getEncodedParam());
    }

    private Map<String, String> getEncodedParam() {
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

    Headers getOkHttpHeaders() {
        final Headers.Builder builder = new Headers.Builder();
        for (Map.Entry<String, String> entry : mHeaders.entrySet()) {
            builder.add(entry.getKey(), entry.getValue());
        }
        return builder.build();
    }

    public ApiResponse execute() {
        return mHttpMethod.execute(this, mNet);
    }

    protected static class UploadFileRequest {
        public File mFile;
        public ProgressListener mListener;
        public String mType;
    }
}
