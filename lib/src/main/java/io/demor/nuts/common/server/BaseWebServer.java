package io.demor.nuts.common.server;

import android.app.Application;
import android.content.res.AssetManager;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import com.x5.template.Chunk;
import com.x5.template.Theme;
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response.Status;
import io.demor.nuts.common.server.annotation.Request;
import io.demor.nuts.common.server.annotation.Url;
import io.demor.nuts.lib.log.L;
import io.demor.nuts.lib.module.BaseResponse;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseWebServer extends NanoHTTPD {

    final HashMap<String, IApiMethod> mApiRequestMap = Maps.newHashMap();
    final HashMap<String, ITemplate> mTemplateMap = Maps.newHashMap();
    final HashMap<String, IResourceApi> mResMap = Maps.newHashMap();
    final AssetManager mAssetManager;
    final AssetTemplateLoader mTemplateLoader;
    final Gson mGson;


    public BaseWebServer(Application application, Gson gson, int port) {
        super(port);
        mGson = gson;
        mAssetManager = application.getAssets();
        mTemplateLoader = new AssetTemplateLoader(mAssetManager);
    }

    public static String toUrlPath(Url origin) {
        if (origin == null) {
            return "";
        }
        return Strings.nullToEmpty(origin.value()).replaceAll("[/\\\\]", "");
    }

    @Override
    public Response serve(final IHTTPSession session) {

        L.v("server:%s", session.getUri());

        final String uri = session.getUri();
        final List<String> path = Splitter.on("/")
                .omitEmptyStrings()
                .trimResults()
                .splitToList(uri);

        if (path.size() == 0) {
            return newFixedLengthResponse(Status.OK, MIME_PLAINTEXT, "Web Debug 4 Android");
        }

        final String firstWordInPath = path.get(0);
        final String remainPath; // "/path1/path2/path3" ==> "path2/path3"
        if (uri.length() < (firstWordInPath.length() + 2)) {
            remainPath = "";
        } else {
            remainPath = uri.substring(firstWordInPath.length() + 2);
        }
        if ("api".equals(firstWordInPath)) {
            final IApiMethod apiMethod = mApiRequestMap.get(remainPath);
            if (apiMethod == null) {
                return newFixedLengthResponse(Status.NOT_FOUND, MIME_JSON, mGson.toJson(BaseResponse.API_NOT_FOUND));
            } else {
                try {
                    final Response response;
                    Map<String, String> files = new HashMap<>();
                    session.parseBody(files);
                    response = newFixedLengthResponse(Status.OK, MIME_JSON, apiMethod.invoke(session.getParms(), files.get("postData").getBytes()));
                    response.addHeader("Access-Control-Allow-Origin", "*");
                    response.addHeader("Access-Control-Allow-Methods", "GET, POST");
                    return response;
                } catch (IOException e) {
                    e.printStackTrace();
                    return newFixedLengthResponse(Status.BAD_REQUEST, MIME_JSON, "");
                } catch (ResponseException e) {
                    e.printStackTrace();
                    return newFixedLengthResponse(Status.BAD_REQUEST, MIME_JSON, "");
                }
            }
        } else if ("web".equals(firstWordInPath)) {
            try {
                if (mTemplateMap.containsKey(remainPath)) {
                    ITemplate template = mTemplateMap.get(remainPath);

                    final Theme theme = new Theme(mTemplateLoader);

                    try {
                        final Chunk chunk = theme.makeChunk(remainPath);
                        for (Map.Entry<String, Object> entry : template.getParam(session.getParms()).entrySet()) {
                            chunk.put(entry.getKey(), entry.getValue());
                        }
                        return newFixedLengthResponse(Status.OK, MIME_HTML, chunk.toString());
                    } catch (Exception e) {
                        return newFixedLengthResponse(Status.INTERNAL_ERROR, MIME_PLAINTEXT, "");
                    }
                } else {
                    final String data = new String(ByteStreams.toByteArray(mAssetManager.open("web/" + remainPath)));
                    L.v("url:%s", session.getUri());
                    Response response = newFixedLengthResponse(Status.OK, null, data);
                    if (uri.endsWith("css")) {
                        response.setMimeType(MIME_CSS);
                    } else if (uri.endsWith("js")) {
                        response.setMimeType(MIME_JS);
                    } else {
                        response.setMimeType(MIME_HTML);
                    }
                    return response;
                }
            } catch (IOException e) {
                L.exception(e);
                return newFixedLengthResponse(Status.NOT_FOUND, MIME_PLAINTEXT, "404 not found");
            }
        } else if ("res".equals(firstWordInPath)) {
            if (mResMap.containsKey(remainPath)) {
                final IResourceApi api = mResMap.get(remainPath);
                InputStream stream = api.getContent(session.getParms());
                if (stream == null) {
                    return newFixedLengthResponse(Status.INTERNAL_ERROR, MIME_PLAINTEXT, "error in get stream");
                }
                return newChunkedResponse(Status.OK, api.mediaType(), stream);
            } else {
                return newFixedLengthResponse(Status.NOT_FOUND, MIME_PLAINTEXT, "not found");
            }
        } else if ("favicon.ico".equals(firstWordInPath)) {
            try {
                return newChunkedResponse(Status.OK, "x-icon", mAssetManager.open("favicon.ico"));
            } catch (IOException e) {
                L.exception(e);
            }
        }
        return newFixedLengthResponse("error");
    }

    public void registerApi(Object api) {
        if (api == null) {
            return;
        }
        final String globalPath = toUrlPath(api.getClass().getAnnotation(Url.class));

        for (java.lang.reflect.Method method : api.getClass().getDeclaredMethods()) {
            final Annotation[][] parameterAnnotations = method.getParameterAnnotations();

            final ReflectApiMethod apiMethod = new ReflectApiMethod(mGson);
            apiMethod.mMethod = method;
            apiMethod.mParamTypeList = method.getParameterTypes();
            apiMethod.mImpl = api;
            apiMethod.mParamList = Lists.newLinkedList();
            for (Annotation[] parameterAnnotation : parameterAnnotations) {
                for (Annotation annotation : parameterAnnotation) {
                    if (annotation instanceof Request) {
                        apiMethod.mParamList.add(((Request) annotation).value());
                    }
                }
            }
            final String urlPath = Joiner.on('/').skipNulls().join(globalPath, toUrlPath(method.getAnnotation(Url.class)));
            L.v("register api:%s class:%s", urlPath, apiMethod.mMethod.getName());
            if (mApiRequestMap.containsKey(urlPath)) {
                throw new MultiPathLoadedException(urlPath);
            } else {
                mApiRequestMap.put(urlPath, apiMethod);
            }
        }
    }

    public void registerApi(IApi api) {
        if (api == null) {
            return;
        }
        final String name = api.name();
        if (mApiRequestMap.containsKey(name)) {
            throw new MultiPathLoadedException(name);
        }
        mApiRequestMap.put(name, new GsonApiImpl(mGson, api));
    }

    public void registerTemplate(final String name, ITemplate template) {
        if (name == null || template == null) {
            return;
        }
        if (mTemplateMap.containsKey(name)) {
            throw new RuntimeException("重复的Template：" + name);
        } else {
            mTemplateMap.put(name, template);
        }
    }

    public void registerResourceApi(String name, IResourceApi api) {
        if (name == null || api == null) {
            return;
        }
        if (mResMap.containsKey(name)) {
            throw new RuntimeException("重复的Resource Api:" + name);
        } else {
            mResMap.put(name, api);
        }
    }

}
