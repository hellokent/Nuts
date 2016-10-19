package io.demor.server;

import android.app.Application;
import com.google.gson.Gson;
import io.demor.nuts.common.server.BaseWebServer;
import io.demor.nuts.common.server.BaseWebSocketServer;

import java.io.IOException;

class Server {

    final BaseWebServer mHttpServer;
    final BaseWebSocketServer mWebSocketServer;

    public Server(final Application application, final Gson gson) {
        mWebSocketServer = new BaseWebSocketServer(0);
        mHttpServer = new BaseWebServer(application, gson, 0);
    }


    public void start(int port) {
        try {
            mHttpServer.setPort(port);
            mHttpServer.start();
            mWebSocketServer.start(Integer.MAX_VALUE);
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
        mWebSocketServer.closeAllConnections();
        mHttpServer.closeAllConnections();
        mWebSocketServer.stop();
        mHttpServer.stop();
    }
}


