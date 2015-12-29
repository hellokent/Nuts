package io.demor.nuts.lib.net;

import com.squareup.okhttp.*;
import io.demor.nuts.lib.net.ApiRequest.UploadFileRequest;

import java.io.File;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public enum HttpMethod {

    GET {
        @Override
        public ApiResponse execute(final ApiRequest request, final INet net) {
            return load(net, new Request.Builder().url(request.mUrl + "?" + request.getURLParam())
                    .headers(request.getOkHttpHeaders())
                    .get()
                    .build());
        }
    },
    POST {
        @Override
        public ApiResponse execute(final ApiRequest request, final INet net) {
            return load(net, new Request.Builder().url(request.mUrl)
                    .headers(request.getOkHttpHeaders())
                    .post(RequestBody.create(CONTENT_TYPE, request.getURLParam()))
                    .build());
        }
    },
    MULTIPART {
        @Override
        public ApiResponse execute(final ApiRequest request, final INet net) {
            final MultipartBuilder multipartBuilder = new MultipartBuilder().type(MultipartBuilder.FORM);

            for (Map.Entry<String, String> entry : request.mParams.entrySet()) {
                multipartBuilder.addFormDataPart(entry.getKey(), entry.getValue());
            }

            for (Map.Entry<String, UploadFileRequest> entry : request.mFiles.entrySet()) {
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

            return load(net, new Request.Builder().url(request.mUrl)
                    .headers(request.getOkHttpHeaders())
                    .post(multipartBuilder.build())
                    .build());
        }
    },

    PUT {
        @Override
        public ApiResponse execute(final ApiRequest request, final INet net) {
            return load(net, new Request.Builder().url(request.mUrl)
                    .headers(request.getOkHttpHeaders())
                    .put(RequestBody.create(CONTENT_TYPE, request.getURLParam()))
                    .build());
        }
    },

    DELETE {
        @Override
        public ApiResponse execute(final ApiRequest request, final INet net) {
            return load(net, new Request.Builder().url(request.mUrl + "?" + request.getURLParam())
                    .headers(request.getOkHttpHeaders())
                    .delete()
                    .build());
        }
    },

    PATCH {
        @Override
        public ApiResponse execute(final ApiRequest request, final INet net) {
            return load(net, new Request.Builder().url(request.mUrl)
                    .headers(request.getOkHttpHeaders())
                    .patch(RequestBody.create(CONTENT_TYPE, request.getURLParam()))
                    .build());
        }
    };

    private static final MediaType CONTENT_TYPE = MediaType.parse("application/x-www-form-urlencoded");

    private static ApiResponse load(INet net, Request request) {
        final OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(net.getConnectionTimeout(), TimeUnit.SECONDS);
        client.setWriteTimeout(net.getWriteTimeout(), TimeUnit.SECONDS);
        client.setReadTimeout(net.getReadTimeout(), TimeUnit.SECONDS);
        try {
            return ApiResponse.ofSuccess(client.newCall(request).execute());
        } catch (Exception e) {
            return ApiResponse.ofFailed(e);
        }
    }

    ApiResponse execute(final ApiRequest request, final INet net) {
        throw new IllegalStateException();
    }


}
