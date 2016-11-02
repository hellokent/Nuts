package io.demor.nuts.common.server;

import android.app.Application;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import com.google.gson.Gson;

import java.io.IOException;

public class Server {

    public final BaseWebServer mHttpServer;
    public final BaseWebSocketServer mWebSocketServer;
    public final WifiManager mWifiManager;

    public Server(final Application application, final Gson gson) {
        mWebSocketServer = new BaseWebSocketServer(0);
        mHttpServer = new BaseWebServer(application, gson, 0);
        mWifiManager = (WifiManager) application.getSystemService(Context.WIFI_SERVICE);
    }

    private static String intToIp(int i) {
        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + ((i >> 24) & 0xFF);
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
        mWebSocketServer.closeAllConnections();
        mHttpServer.closeAllConnections();
        mWebSocketServer.stop();
        mHttpServer.stop();
    }

    public String getIpAddress() {
        if (!mWifiManager.isWifiEnabled()) {
            return "";
        }
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();

        if (wifiInfo == null) {
            return "";
        }

        return intToIp(wifiInfo.getIpAddress());
    }
}


