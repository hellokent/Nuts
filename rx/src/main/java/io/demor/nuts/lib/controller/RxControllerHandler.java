package io.demor.nuts.lib.controller;

import rx.Observable;
import rx.Subscriber;

import java.lang.reflect.Method;

public class RxControllerHandler<I> extends ControllerInvokeHandler<I> {

    public RxControllerHandler(I impl) {
        super(impl);
    }

    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        final Class<?> returnClz = method.getReturnType();
        if (returnClz.isAssignableFrom(Observable.class)) {
            return Observable.create(new Observable.OnSubscribe<I>() {
                @Override
                public void call(Subscriber<? super I> subscriber) {
                    subscriber.onStart();
                    try {
                        final Object resp = new ControllerCallable(method, mImpl, args).call();
                        subscriber.onNext((I) resp);
                    } catch (Exception e) {
                        subscriber.onError(e);
                    }
                    subscriber.onCompleted();
                }
            });
        } else {
            return super.invoke(proxy, method, args);
        }
    }
}
