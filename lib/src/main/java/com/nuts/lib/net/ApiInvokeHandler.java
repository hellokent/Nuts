package com.nuts.lib.net;

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
import com.nuts.lib.BuildConfig;
import com.nuts.lib.ReflectUtils;
import com.nuts.lib.annotation.net.Get;
import com.nuts.lib.annotation.net.Header;
import com.nuts.lib.annotation.net.Multipart;
import com.nuts.lib.annotation.net.Param;
import com.nuts.lib.annotation.net.Path;
import com.nuts.lib.annotation.net.Post;
import com.nuts.lib.annotation.net.Retry;
import com.nuts.lib.controller.Return;
import com.nuts.lib.log.TimingLogger;
import com.nuts.lib.net.NetBuilder.HttpMethod;

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
        final Class<?> returnClz = method.getReturnType();
        final Header header = method.getAnnotation(Header.class);
        final int tryCount = method.getAnnotation(Retry.class) == null ? 1 : method.getAnnotation(Retry.class).value();

        final String url;
        if (get != null) {
            url = get.value();
        } else if (multipart != null) {
            url = multipart.value();
        } else {
            url = post.value();
        }

        final Class<?> respClz = method.getReturnType();

        final ArrayList<String> pathArgs = Lists.newArrayList();
        if (!ReflectUtils.isSubclassOf(respClz, IResponse.class) && ReflectUtils.checkGenericType(method.getGenericReturnType(), IResponse.class)) {
            throw new InvalidParameterException("API:" + method.getName() + "，返回值必须继承IResponse");
        }
        final HttpMethod m;
        if (get != null) {
            m = HttpMethod.GET;
        } else if (multipart != null) {
            m = HttpMethod.MULTIPART;
        } else {
            m = HttpMethod.POST;
        }
        final NetBuilder builder = new NetBuilder(mGson, mNet, url,
                ReflectUtils.isSubclassOf(returnClz, Return.class) ? (Class<?>) ReflectUtils.getGenericType(method
                        .getGenericReturnType()) : respClz, m,
                method, args);

        if (header != null) {
            for (final String header1 : header.value()) {
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
        for (int i = 0, n = args == null ? 0 : args.length; i < n; ++i) {
            final Annotation annotation = annotations[i][0];
            if (annotation instanceof Param) {
                final Param param = (Param) annotation;
                if (args[i] == null) {
                    builder.addParam(param, "");
                } else {
                    builder.addParam(param, args[i]);
                }
            } else if (annotation instanceof Path){
                pathArgs.add(args[i] == null ? "" : args[i].toString());
            }
        }

        builder.setUrl(String.format(url, pathArgs.toArray()));

        final Callable<IResponse> callable = new Callable<IResponse>() {
            @Override
            public IResponse call() {
                final TimingLogger timingLogger = new TimingLogger("api", url);
                timingLogger.addSplit("parse params");
                int count = Math.max(0, tryCount);
                builder.initParam();

                NetResult result;
                final ApiProcess process = new ApiProcess(builder);
                do {
                    result = mCallback.handle(process, builder.mUrl, builder.mParams, builder.mHeaders, builder
                            .mMethod.getName());
                    --count;
                    timingLogger.addSplit("network(" + count + ")");
                    result.mIResponse.setErrorCode(result.mStatusCode);
                } while (count > 0 && !result.mIsSuccess);

                if (BuildConfig.DEBUG) {
                    timingLogger.dumpToLog();
                }

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
