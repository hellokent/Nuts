package io.demor.nuts.lib.eventbus;

import io.demor.nuts.lib.controller.AppInstance;
import io.demor.nuts.lib.controller.ControllerUtil;
import io.demor.nuts.lib.module.PushObject;

import java.util.concurrent.TimeUnit;

public class ListenerBarrier extends BaseBarrier {

    private int mTimeout = 10;
    private TimeUnit mUnit = TimeUnit.SECONDS;

    public ListenerBarrier(final AppInstance appInstance) throws Exception {
        super(appInstance);
    }

    public void setTimeout(int timeout, TimeUnit unit) {
        mTimeout = timeout;
        mUnit = unit;
    }

    public void register(final Object o) {
        Class<?>[] classes = o.getClass().getInterfaces();
        if (classes == null || classes.length == 0) {
            return;
        }
        new Thread() {
            @Override
            public void run() {
                waitForAll(mTimeout, mUnit, new PushHandler() {
                    @Override
                    public void onReceivePush(final PushObject object) {
                        if (object == null || object.mType != PushObject.TYPE_LISTENER) {
                            return;
                        }
                        ControllerUtil.ControllerMethodInfo info = ControllerUtil.GSON.fromJson(object.mData.toString(), ControllerUtil.ControllerMethodInfo.class);
                        boolean in = false;
                        for (Class<?> i : o.getClass().getInterfaces()) {
                            if (info.mClz.equals(i.getName())) {
                                in = true;
                                break;
                            }
                        }
                        if (!in) {
                            return;
                        }
                        try {
                            System.out.println(object.mData);
                            ControllerUtil.callMethodNative(o, object.mData.toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }.start();
    }
}
