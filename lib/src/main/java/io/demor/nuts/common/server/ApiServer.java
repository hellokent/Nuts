package io.demor.nuts.common.server;

import android.app.Application;
import com.google.common.base.Joiner;
import io.demor.nuts.lib.controller.ControllerUtil;
import io.demor.nuts.lib.module.ApiResponse;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public final class ApiServer {

    public Server mServer;

    public ApiServer(Application application) {
        mServer = new Server(application, ControllerUtil.MODULE_GSON);
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
                    return ApiResponse.ofSuccess(ControllerUtil.callControllerNative(impl, body));
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                    return ApiResponse.ofFailed("no such method");
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                    return ApiResponse.ofFailed("invoke target exception:" + e.getMessage() + "\n" +
                            Joiner.on('\n').join(e.getTargetException().getStackTrace()));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    return ApiResponse.ofFailed("illegal access exception:" + e.getMessage());
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    return ApiResponse.ofFailed("class not found:" + e.getMessage());
                }
            }
        });
        return this;
    }

    public void start() {
        start(0);
    }

    public void start(int port) {
        mServer.start(port);
    }
}
