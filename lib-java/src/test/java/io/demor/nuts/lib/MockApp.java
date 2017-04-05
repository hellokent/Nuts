package io.demor.nuts.lib;

import com.x5.template.providers.TemplateProvider;

import java.io.IOException;
import java.io.InputStream;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.concurrent.Executor;

import io.demor.nuts.lib.eventbus.EventBus;
import io.demor.nuts.lib.eventbus.ListenerBus;
import io.demor.nuts.lib.server.ApiServer;
import io.demor.nuts.lib.server.BaseWebSocketServer;
import io.demor.nuts.lib.server.ConfigServer;
import io.demor.nuts.lib.server.IClient;

public class MockApp implements IClient, Executor{

    public ApiServer mServer;
    private String mAppId;
    public static ListenerBus sListenerBus;
    public static EventBus sEventBus;

    public MockApp(String appId) throws IOException {
        mAppId = appId;
        mServer = new MockApiServer(this, new BaseWebSocketServer(0));
        mServer.mHttpServer.start();
        mServer.mWebSocketServer.start();
        ConfigServer.initConfig(this, mServer);
        sListenerBus = new ListenerBus(this, this, mServer);
        sEventBus = new EventBus(this, this);
        mServer.registerEventBus(sEventBus);
        mServer.registerListenBus();
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

    @Override
    public String getAppId() {
        return mAppId;
    }

    @Override
    public void execute(Runnable runnable) {
        runnable.run();
    }
}
