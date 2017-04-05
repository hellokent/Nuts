package io.demor.nuts.lib.server;

import com.google.gson.Gson;

import java.io.IOException;

public class Server {

    public BaseWebServer mHttpServer;
    public BaseWebSocketServer mWebSocketServer;
    protected IClient mClient;

    public Server(final IClient client, final Gson gson) {
        mClient = client;
        mWebSocketServer = new BaseWebSocketServer(0);
        mHttpServer = new BaseWebServer(client, gson, 0);
    }

    public void start() {
        try {
            mHttpServer.start(0);
            mWebSocketServer.start(0);
        } catch (IOException ignored) {
        }
    }

    public int getHttpPort() {
        return mHttpServer.getListeningPort();
    }

    public int getWebSocketPort() {
        return mWebSocketServer.getListeningPort();
    }

    public void shutdown() {
        if (mWebSocketServer != null) {
            mWebSocketServer.closeAllConnections();
            mWebSocketServer.stop();
        }
        if (mHttpServer != null) {
            mHttpServer.closeAllConnections();
            mHttpServer.stop();
        }
    }

    public String getIpAddress() {
        return mClient.getIpAddress();
    }
}


