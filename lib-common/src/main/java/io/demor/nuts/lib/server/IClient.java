package io.demor.nuts.lib.server;

import com.x5.template.providers.TemplateProvider;

import java.io.IOException;
import java.io.InputStream;

public interface IClient {
    InputStream getResource(String name) throws IOException;

    TemplateProvider getTemplateProvider();

    String getIpAddress();

    String getAppId();
}
