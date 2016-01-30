package io.demor.nuts.lib.api;

import io.demor.nuts.lib.ReflectUtils;
import io.demor.nuts.lib.net.ApiInvokeHandler;
import io.demor.nuts.lib.net.ApiRequest;
import io.demor.nuts.lib.net.ApiResponse;
import io.demor.nuts.lib.net.INet;
import rx.Observable;
import rx.Subscriber;

import java.lang.reflect.Method;

public class RxApiHandler extends ApiInvokeHandler {

    public RxApiHandler(INet net) {
        super(net);
    }

    @Override
    protected Object methodReturn(final ApiRequest request, final int retryCount, final Method method, final Object[] args) throws Exception {
        final Class<?> returnClz = method.getReturnType();

        if (returnClz.isAssignableFrom(Observable.class)) {
            return Observable.create(new Observable.OnSubscribe<Object>() {
                @Override
                public void call(Subscriber<? super Object> subscriber) {
                    subscriber.onStart();
                    final ApiResponse response = execute(request, retryCount, method, args);
                    if (response.isSuccess()) {
                        subscriber.onNext(mNet.createResponse((Class) ReflectUtils.getGenericType(method.getGenericReturnType()), response));
                    } else {
                        subscriber.onError(response.getException());
                    }
                    subscriber.onCompleted();
                }
            });
        } else {
            return super.methodReturn(request, retryCount, method, args);
        }
    }
}
