package io.demor.nuts.lib.server;

import com.google.common.collect.Maps;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import io.demor.nuts.lib.controller.AppInstance;
import io.demor.nuts.lib.controller.MethodInfoUtil;
import io.demor.nuts.lib.module.AppInstanceResponse;
import io.demor.nuts.lib.module.BaseResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class ConfigServer {
    public static final int CONFIG_SERVER_PORT = 8080;
    private static final HashMap<String, AppInstance> APPLICATION_MAP = Maps.newHashMap();

    public static void initConfig(final IClient client, Server server) throws IOException {
        BaseWebServer localServer = new BaseWebServer(client, MethodInfoUtil.GSON, CONFIG_SERVER_PORT);
        localServer.registerApi(new IApi() {
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
        localServer.registerApi(new IApi() {
            @Override
            public String name() {
                return "updateApp";
            }

            @Override
            public Object call(final Map<String, String> param, final String body) {
                final AppInstance instance = new AppInstance(
                        client.getIpAddress(),
                        Integer.parseInt(param.get("http")),
                        Integer.parseInt(param.get("websocket")));
                APPLICATION_MAP.put(param.get("app"), instance);
                return new BaseResponse();
            }
        });
        try {
            localServer.start();
            APPLICATION_MAP.put(client.getAppId(), new AppInstance(client.getIpAddress(), server.getHttpPort(), server.getWebSocketPort()));
        } catch (IOException e) {
            e.printStackTrace();
            new OkHttpClient()
                    .newCall(new Request.Builder()
                            .url(String.format(Locale.getDefault(), "http://%s:%d/api/updateApp?app=%s&http=%d&websocket=%d",
                                    client.getIpAddress(), CONFIG_SERVER_PORT,
                                    client.getAppId(), server.getHttpPort(), server.getWebSocketPort()))
                            .build())
                    .execute();

        }
    }
}
