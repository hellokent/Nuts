package io.demor.nuts.lib.net;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import io.demor.nuts.lib.ReflectUtils;
import io.demor.nuts.lib.annotation.net.Delete;
import io.demor.nuts.lib.annotation.net.Get;
import io.demor.nuts.lib.annotation.net.Header;
import io.demor.nuts.lib.annotation.net.Headers;
import io.demor.nuts.lib.annotation.net.Multipart;
import io.demor.nuts.lib.annotation.net.Param;
import io.demor.nuts.lib.annotation.net.Patch;
import io.demor.nuts.lib.annotation.net.Path;
import io.demor.nuts.lib.annotation.net.Post;
import io.demor.nuts.lib.annotation.net.Put;
import io.demor.nuts.lib.annotation.net.Retry;
import io.demor.nuts.lib.controller.Return;
import io.demor.nuts.lib.net.NetBuilder.HttpMethod;

public class ApiInvokeHandler implements InvocationHandler {

    public final INet mNet;
    public final Gson mGson;

    public ApiCallback mCallback = new ApiCallback();

    public ApiInvokeHandler(final INet net, final Gson gson) {
        mNet = net;
        mGson = gson;
    }

    public ApiInvokeHandler setApiCallback(ApiCallback callback) {
        mCallback = callback;
        return this;
    }

    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        final Get get = method.getAnnotation(Get.class);
        final Post post = method.getAnnotation(Post.class);
        final Multipart multipart = method.getAnnotation(Multipart.class);
        final Patch patch = method.getAnnotation(Patch.class);
        final Put put = method.getAnnotation(Put.class);
        final Delete delete = method.getAnnotation(Delete.class);

        final Class<?> returnClz = method.getReturnType();
        final Headers headers = method.getAnnotation(Headers.class);
        final int tryCount = method.getAnnotation(Retry.class) == null ? 1 : method.getAnnotation(Retry.class).value();

        final String url;
        final HttpMethod m;

        if (get != null) {
            url = get.value();
            m = HttpMethod.GET;
        } else if (multipart != null) {
            url = multipart.value();
            m = HttpMethod.MULTIPART;
        } else if (post != null) {
            url = post.value();
            m = HttpMethod.POST;
        } else if (patch != null) {
            url = patch.value();
            m = HttpMethod.PATCH;
        } else if (delete != null) {
            url = delete.value();
            m = HttpMethod.DELETE;
        } else if (put != null) {
            url = put.value();
            m = HttpMethod.PUT;
        } else {
            throw new RuntimeException("method:" + method.getName() + "in interface:" + proxy.getClass()
                    .getName() + " is invalid method because of no HTTP METHOD annotation");
        }

        final ArrayList<String> pathArgs = Lists.newArrayList();
        if (!ReflectUtils.isSubclassOf(returnClz, IResponse.class) && ReflectUtils.checkGenericType(method
                .getGenericReturnType(), IResponse.class)) {
            throw new InvalidParameterException("API:" + method.getName() + "，返回值必须继承IResponse");
        }
        final NetBuilder builder = new NetBuilder(mGson, mNet, url, ReflectUtils.isSubclassOf(returnClz, Return
                .class) ? (Class<?>) ReflectUtils.getGenericType(method.getGenericReturnType()) : returnClz, m,
                method, args);

        if (headers != null) {
            for (final String header1 : headers.value()) {
                final List<String> pair = Splitter.on(":")
                        .trimResults()
                        .splitToList(header1);
                if (pair.size() < 2) {
                    continue;
                }
                builder.mHeaders.put(pair.get(0), pair.get(1));
            }
        }

        final Annotation[][] annotations = method.getParameterAnnotations();
        if (annotations.length != 0) {
            for (int i = 0, n = args == null ? 0 : args.length; i < n; ++i) {
                if (annotations[i].length == 0) {
                    continue;
                }
                final Annotation annotation = annotations[i][0];
                if (annotation instanceof Param) {
                    final Param param = (Param) annotation;
                    if (args[i] == null) {
                        builder.addParam(param, "");
                    } else {
                        builder.addParam(param, args[i]);
                    }
                } else if (annotation instanceof Path) {
                    pathArgs.add(args[i] == null ? "" : args[i].toString());
                } else if (annotation instanceof Header) {
                    builder.mHeaders.put(((Header) annotation).value(), args[i].toString());
                }
            }
        }

        builder.setUrl(String.format(url, pathArgs.toArray()));

        final Callable<IResponse> callable = new Callable<IResponse>() {
            @Override
            public IResponse call() {
                int count = Math.max(0, tryCount);
                builder.initParam();
                NetResult result;
                final ApiProcess process = new ApiProcess(builder);
                do {
                    result = mCallback.handle(process, builder.mUrl, builder.mParams, builder.mHeaders, builder
                            .mMethod.getName());
                    --count;
                } while (count > 0 && !result.mIsSuccess);
                return result.mIResponse;
            }
        };

        if (ReflectUtils.isSubclassOf(returnClz, Return.class)) {
            return returnClz.getConstructor(Callable.class, Method.class)
                    .newInstance(callable, method);
        } else {
            return callable.call();
        }
    }
}
