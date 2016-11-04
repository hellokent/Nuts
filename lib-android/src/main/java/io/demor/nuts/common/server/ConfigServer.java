package io.demor.nuts.common.server;

import android.app.Application;
import com.google.common.collect.Maps;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import io.demor.nuts.lib.NutsApplication;
import io.demor.nuts.lib.controller.AppInstance;
import io.demor.nuts.lib.controller.ControllerUtil;
import io.demor.nuts.lib.log.L;
import io.demor.nuts.lib.module.AppInstanceResponse;
import io.demor.nuts.lib.module.BaseResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public final class ConfigServer {
    static final int CONFIG_SERVER_PORT = 8080;
    static final HashMap<String, AppInstance> APPLICATION_MAP = Maps.newHashMap();
    static BaseWebServer sServer;

    public static void initConfig(final Application application, Server server) throws IOException {
        sServer = new BaseWebServer(application, ControllerUtil.GSON, CONFIG_SERVER_PORT);
        sServer.registerApi(new IApi() {
            @Override
            public String name() {
                return "application";
            }

            @Override
            public Object call(final Map<String, String> param, final String body) {
                final AppInstanceResponse response = new AppInstanceResponse();
                response.mInstance = APPLICATION_MAP.get(param.get("name"));
                return response;
            }
        });
        sServer.registerApi(new IApi() {
            @Override
            public String name() {
                return "updateApp";
            }

            @Override
            public Object call(final Map<String, String> param, final String body) {
                final AppInstance instance = new AppInstance(
                        NutsApplication.getIpAddress(),
                        Integer.parseInt(param.get("http")),
                        Integer.parseInt(param.get("websocket")));
                APPLICATION_MAP.put(param.get("app"), instance);
                return new BaseResponse();
            }
        });
        try {
            sServer.start();
            APPLICATION_MAP.put(application.getPackageName(), new AppInstance(NutsApplication.getIpAddress(), server.getHttpPort(), server.getWebSocketPort()));
        } catch (IOException e) {
            L.exception(e);
            new OkHttpClient()
                    .newCall(new Request.Builder()
                            .url(String.format("http://%s:%d/api/updateApp?app=%s&http=%d&websocket=%d",
                                    NutsApplication.getIpAddress(), CONFIG_SERVER_PORT,
                                    application.getPackageName(), server.getHttpPort(), server.getWebSocketPort()))
                            .build())
                    .execute();

        }
    }
}
