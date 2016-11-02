package io.demor.nuts.lib.controller;

public class AppInstance {
    public String mHost;
    public int mHttpPort;
    public int mSocketPort;

    public AppInstance(final String host, final int httpPort, final int socketPort) {
        mHost = host;
        mHttpPort = httpPort;
        mSocketPort = socketPort;
    }
}
