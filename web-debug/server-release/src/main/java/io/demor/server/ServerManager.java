package io.demor.server;

import android.app.Application;
import android.content.Context;
import io.demor.server.sniff.NetworkSniffer;
import io.demor.server.sniff.SimpleSniffer;

public class ServerManager {

    public static void init(Application application) {

    }

    public static String getIpAddress() {
        return "";
    }

    public synchronized static void start() {
    }

    public synchronized static void start(int port) {
    }

    public synchronized static void stop() {
    }

    public static SimpleSniffer getSniffer(String tag) {
        return new SimpleSniffer(tag, null);
    }

    public synchronized static NetworkSniffer getNetworkSniffer() {
        return new NetworkSniffer(null);
    }

    public static void showAddressDialog(Context context) {

    }


    public static int getHttpPort() {
        return 0;
    }

    public static int getWebSocketPort() {
        return 0;
    }
}
