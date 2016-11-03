package io.demor.nuts.common.server;

import android.app.Application;
import com.google.common.base.Joiner;
import io.demor.nuts.lib.controller.ControllerUtil;
import io.demor.nuts.lib.eventbus.BaseEvent;
import io.demor.nuts.lib.eventbus.EventBus;
import io.demor.nuts.lib.eventbus.IPostListener;
import io.demor.nuts.lib.log.L;
import io.demor.nuts.lib.module.BaseResponse;
import io.demor.nuts.lib.module.ControllerInvocationResponse;
import io.demor.nuts.lib.module.PushObject;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import static io.demor.nuts.lib.controller.ControllerUtil.GSON;

public final class ApiServer {

    public Server mServer;
    public Application mApplication;
    public boolean mCanSendListener;

    public ApiServer(Application application) {
        mServer = new Server(application, GSON);
        mApplication = application;
    }

    public <T> ApiServer registerController(final Class<T> api, final T impl) {
        mServer.mHttpServer.registerApi(new IApi() {
            @Override
            public String name() {
                return "controller/" + api.getName();
            }

            @Override
            public Object call(Map<String, String> param, String body) {
                try {
                    final ControllerInvocationResponse response = new ControllerInvocationResponse();
                    response.mData = ControllerUtil.parseMethodInfo(impl, body).callController();
                    return response;
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                    return new BaseResponse().ofFailed("no such method");
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                    return new BaseResponse().ofFailed("invoke target exception:" + e.getMessage() + "\n" +
                            Joiner.on('\n').join(e.getTargetException().getStackTrace()));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    return new BaseResponse().ofFailed("illegal access exception:" + e.getMessage());
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    return new BaseResponse().ofFailed("class not found:" + e.getMessage());
                }
            }
        });
        return this;
    }

    public ApiServer registerEventBus(final EventBus eventBus) {
        eventBus.setPostListener(new IPostListener() {
            @Override
            public void onPostEvent(BaseEvent o) {
                final PushObject pushObject = new PushObject();
                pushObject.mType = PushObject.TYPE_EVENT;
                pushObject.mDataClz = o.getClass().getName();
                pushObject.mData = o;
                mServer.mWebSocketServer.sendMessage(GSON.toJson(pushObject));
            }
        });
        return this;
    }

    public ApiServer registerListenBus() {
        mCanSendListener = true;
        return this;
    }

    public void sendListenerMethod(String info) {
        final PushObject pushObject = new PushObject();
        pushObject.mType = PushObject.TYPE_LISTENER;
        pushObject.mDataClz = String.class.getName();
        pushObject.mData = info;
        mServer.mWebSocketServer.sendMessage(GSON.toJson(pushObject));
    }

    public void start() {
        mServer.start();
        new Thread("config-server") {
            @Override
            public void run() {
                try {
                    ConfigServer.initConfig(mApplication, mServer);
                } catch (IOException e) {
                    L.exception(e);
                }
            }
        }.start();
    }
}
