package io.demor.server.res;

import fi.iki.elonen.NanoHTTPD;
import io.demor.nuts.lib.log.Logger;
import io.demor.nuts.lib.log.LoggerFactory;
import io.demor.nuts.lib.server.IResourceApi;
import io.demor.server.ScreenHelper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class ScreenImageResourceApi implements IResourceApi {

    static final Logger LOGGER = LoggerFactory.getLogger(ScreenImageResourceApi.class);

    @Override
    public InputStream getContent(Map<String, String> param) {
        try {
            byte[] bytes = ScreenHelper.getScreenBytes();
            if (bytes == null) {
                return null;
            }
            return new ByteArrayInputStream(bytes);
        } catch (IOException e) {
            LOGGER.e("error in get Screen file stream", e);
            return null;
        }
    }

    @Override
    public String mediaType() {
        return NanoHTTPD.MIME_IMAGE;
    }
}
