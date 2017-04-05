package io.demor.nuts.lib;

import com.google.common.base.Splitter;

import java.util.List;

import fi.iki.elonen.NanoHTTPD;
import io.demor.nuts.lib.controller.AppInstance;
import io.demor.nuts.lib.controller.ControllerUtil;
import io.demor.nuts.lib.module.AppInstanceResponse;

public class MockApp extends NanoHTTPD{

    public MockApp() {
        super(8888);
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
}
