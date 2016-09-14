package io.demor.nuts.lib.api;

import com.google.common.base.Splitter;
import com.google.common.io.CharStreams;
import fi.iki.elonen.NanoHTTPD;
import io.demor.nuts.lib.model.AppInstance;
import io.demor.nuts.lib.model.GsonObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import static fi.iki.elonen.NanoHTTPD.Response.Status;

public class ApiServer extends NanoHTTPD {

    final AppInstance mInstance;

    public ApiServer(final AppInstance instance) {
        super(0);
        mInstance = instance;
    }

    @Override
    public Response serve(IHTTPSession session) {
        super.serve(session);
        final List<String> uriList =  Splitter.on('/')
                .omitEmptyStrings()
                .trimResults()
                .splitToList(session.getUri());
        try {
            final String body = CharStreams.toString(new InputStreamReader(session.getInputStream()));
            switch (uriList.get(0)) {
                case "c": //controller
                    return newFixedLengthResponse(TestClient.GSON.toJson(
                            new GsonObject(
                                    mInstance.invokeController(
                                            uriList.get(1),
                                            uriList.get(2),
                                            TestClient.GSON.fromJson(body, GsonObject[].class)))));
                case "e": //eventbus
                    break;
                case "s": //storage
                    break;
                default:
                    return newFixedLengthResponse(Status.NOT_FOUND, NanoHTTPD.MIME_JSON, "");
            }
            return newFixedLengthResponse(Status.NOT_FOUND, "", "");
        } catch (IOException e) {
            e.printStackTrace();
            return newFixedLengthResponse(Status.INTERNAL_ERROR, "", "");
        }
    }
}
