package io.demor.server.sniff;

import android.os.Handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NetworkSniffer extends SimpleSniffer {

    public NetworkSniffer(final Handler handler) {
        super("network", handler);
    }

    public SessionWatcher createWatcher() {
        return new SessionWatcher();
    }

    static class Request {
        public int id;
        public String url;
        public String method;
        public List<Param> header = new ArrayList<>();
        public List<Param> param = new ArrayList<>();
    }

    static class Response {
        public int id;
        public int statusCode;
        public List<Param> header = new ArrayList<>();
        public String response;
        public String exception;

    }

    static class Param {
        public String key;
        public String value;

        public Param(final String key, final String value) {
            this.key = key;
            this.value = value;
        }
    }

    public class SessionWatcher {


        SessionWatcher() {
        }

        public void watchRequest(String url, Map<String, ?> param, Map<String, String> header, String method) {
        }

        public void watchResponse(int statusCode, String msg, Map<String, String> header) {
        }

        public void watchException(Throwable throwable) {
        }
    }

}
