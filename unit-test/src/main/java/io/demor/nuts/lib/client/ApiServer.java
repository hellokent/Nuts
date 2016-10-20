package io.demor.nuts.lib.client;

import com.google.gson.Gson;
import io.demor.nuts.common.server.BaseWebServer;

public class ApiServer extends BaseWebServer {

    final AppInstance mInstance;

    public ApiServer(final AppInstance instance) {
        super(instance.mApplication, new Gson(), 0);
        mInstance = instance;
    }
}
