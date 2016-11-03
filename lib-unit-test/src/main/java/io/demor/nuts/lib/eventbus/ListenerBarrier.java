package io.demor.nuts.lib.eventbus;

import io.demor.nuts.lib.controller.AppInstance;
import io.demor.nuts.lib.controller.ControllerUtil;
import io.demor.nuts.lib.module.PushObject;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;

import static io.demor.nuts.lib.controller.ControllerUtil.parseMethodInfo;

public class ListenerBarrier extends BaseBarrier {

    public ListenerBarrier(final AppInstance appInstance) throws Exception {
        super(appInstance);
    }

    public void registerOnce(final Object o, final long time, final TimeUnit unit) {
        Class<?>[] classes = o.getClass().getInterfaces();
        if (classes == null || classes.length == 0) {
            throw new IllegalArgumentException();
        }
        new Thread() {
            @Override
            public void run() {
                final PushObject pushObject = waitForSingle(time, unit, new PushFilter() {
                    @Override
                    public boolean checkPush(final PushObject object) {
                        return checkPushObject(object, o);
                    }
                });
                if (pushObject == null) {
                    return;
                }
                try {
                    parseMethodInfo(o, pushObject.mData.toString()).callImpl();
                } catch (NoSuchMethodException | InvocationTargetException | ClassNotFoundException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void registerDuring(final Object o, final long time, final TimeUnit unit) {
        Class<?>[] classes = o.getClass().getInterfaces();
        if (classes == null || classes.length == 0) {
            throw new IllegalArgumentException();
        }
        new Thread() {
            @Override
            public void run() {
                waitForAll(time, unit, new PushHandler() {
                    @Override
                    public void onReceivePush(final PushObject object) {
                        if (checkPushObject(object, o)) {
                            try {
                                parseMethodInfo(o, object.mData.toString()).callImpl();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        }.start();
    }

    private boolean checkPushObject(PushObject object, Object o) {
        if (object == null || object.mType != PushObject.TYPE_LISTENER) {
            return false;
        }
        ControllerUtil.ControllerMethodInfo methodInfo = ControllerUtil.GSON.fromJson(object.mData.toString(), ControllerUtil.ControllerMethodInfo.class);
        boolean in = false;
        for (Class<?> i : o.getClass().getInterfaces()) {
            if (methodInfo.mClz.equals(i.getName())) {
                in = true;
                break;
            }
        }
        return in;

    }
}
