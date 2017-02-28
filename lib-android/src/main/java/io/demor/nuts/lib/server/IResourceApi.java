package io.demor.nuts.lib.server;

import java.io.InputStream;
import java.util.Map;

public interface IResourceApi {

    InputStream getContent(Map<String, String> param);

    String mediaType();
}
