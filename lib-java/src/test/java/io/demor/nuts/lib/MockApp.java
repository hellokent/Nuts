package io.demor.nuts.lib;

import com.google.common.base.Splitter;
import com.x5.template.providers.TemplateProvider;

import java.io.IOException;
import java.io.InputStream;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.List;

import fi.iki.elonen.NanoHTTPD;
import io.demor.nuts.lib.controller.AppInstance;
import io.demor.nuts.lib.controller.ControllerUtil;
import io.demor.nuts.lib.module.AppInstanceResponse;
import io.demor.nuts.lib.server.IClient;
import io.demor.nuts.lib.server.Server;

public class MockApp extends NanoHTTPD implements IClient{

    Server mServer;


    public MockApp() {
        super(8888);
        mServer = new Server(this, ControllerUtil.GSON);
    }

    @Override
    public Response serve(IHTTPSession session) {
        final String uri = session.getUri();
        final List<String> path = Splitter.on("/")
                .omitEmptyStrings()
                .trimResults()
                .splitToList(uri);

        if (uri.startsWith("api/application")) {
            AppInstanceResponse response = new AppInstanceResponse();
            response.mInstance = new AppInstance("localhost", 8888, 8889);
            return newFixedLengthResponse(Response.Status.OK, MIME_JSON, ControllerUtil.GSON.toJson(response));
        }

        return super.serve(session);

    }

    @Override
    public InputStream getResource(String name) throws IOException {
        throw new IOException("nothing in lib-java");
    }

    @Override
    public TemplateProvider getTemplateProvider() {
        return null;
    }

    @Override
    public String getIpAddress() {
        try {
            return Inet4Address.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return "";
        }
    }
}
