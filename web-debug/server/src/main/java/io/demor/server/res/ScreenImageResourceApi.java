package io.demor.server.res;

import fi.iki.elonen.NanoHTTPD;
import io.demor.nuts.common.server.IResourceApi;
import io.demor.nuts.lib.log.L;
import io.demor.server.ScreenHelper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class ScreenImageResourceApi implements IResourceApi {
    @Override
    public InputStream getContent(Map<String, String> param) {
        try {
            byte[] bytes = ScreenHelper.getScreenBytes();
            if (bytes == null) {
                return null;
            }
            return new ByteArrayInputStream(bytes);
        } catch (IOException e) {
            L.e("error in get Screen file stream", e);
            return null;
        }
    }

    @Override
    public String mediaType() {
        return NanoHTTPD.MIME_IMAGE;
    }
}
