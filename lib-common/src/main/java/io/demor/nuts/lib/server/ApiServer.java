package io.demor.nuts.lib.server;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import io.demor.nuts.lib.eventbus.BaseEvent;
import io.demor.nuts.lib.eventbus.EventBus;
import io.demor.nuts.lib.eventbus.IPostListener;
import io.demor.nuts.lib.module.BaseResponse;
import io.demor.nuts.lib.module.ControllerInvocationResponse;
import io.demor.nuts.lib.module.PushObject;
import io.demor.nuts.lib.module.StorageResponse;
import io.demor.nuts.lib.storage.Storage;

import static io.demor.nuts.lib.controller.ControllerUtil.GSON;
import static io.demor.nuts.lib.controller.ControllerUtil.parseMethodInfo;

public final class ApiServer extends Server {

    public boolean mCanSendListener;

    public ApiServer(IClient client, BaseWebSocketServer webSocketServer) {
        super(client, GSON);
        mWebSocketServer = webSocketServer;
    }

    public <T> ApiServer registerController(final Class<T> api, final T impl) {
        mHttpServer.registerApi(new IApi() {
            @Override
            public String name() {
                return "controller/" + api.getName();
            }

            @Override
            public Object call(Map<String, String> param, String body) {
                try {
                    return new ControllerInvocationResponse(parseMethodInfo(impl, body).callController());
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

    public <T> ApiServer registerStorage(final Class<T> typeClz, final Storage<T> storage) {
        mHttpServer.registerApi(new IApi() {
            @Override
            public String name() {
                return "storage/" + typeClz.getName();
            }

            @Override
            public Object call(final Map<String, String> param, final String body) {
                final String action = param.get("action");
                if (Strings.isNullOrEmpty(action)) {
                    return new BaseResponse().ofFailed("empty action");
                }
                StorageResponse response;
                switch (action) {
                    case "get":
                        response = new StorageResponse();
                        response.mData = GSON.toJson(storage.get());
                        return response;
                    case "save":
                        storage.save(GSON.fromJson(body, typeClz));
                        return new BaseResponse();
                    case "delete":
                        storage.delete();
                        return new BaseResponse();
                    case "contains": {
                        response = new StorageResponse();
                        response.mData = Boolean.toString(storage.contains());
                        return response;
                    }
                    default:
                        return new BaseResponse().ofFailed("invalidate action");
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
                mWebSocketServer.sendPushObj(pushObject);
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
        mWebSocketServer.sendPushObj(pushObject);
    }

    public void start() {
        super.start();
        new Thread("config-server") {
            @Override
            public void run() {
                try {
                    ConfigServer.initConfig(mClient, ApiServer.this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
