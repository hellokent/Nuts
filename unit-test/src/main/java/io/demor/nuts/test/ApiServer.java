package io.demor.nuts.test;

import fi.iki.elonen.NanoHTTPD;

public class ApiServer extends NanoHTTPD {

    public ApiServer() {
        super(0);
    }

    @Override
    public Response serve(IHTTPSession session) {

        return super.serve(session);
    }
}
