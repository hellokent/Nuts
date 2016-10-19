package io.demor.server.sniff;

import android.os.Handler;
import android.util.Log;
import com.google.common.collect.Lists;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

public class NetworkSniffer extends SimpleSniffer {

    static int sCount = 0;

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
        public List<Param> header = Lists.newArrayList();
        public List<Param> param = Lists.newArrayList();
    }

    static class Response {
        public int id;
        public int statusCode;
        public List<Param> header = Lists.newArrayList();
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

        int mId;

        SessionWatcher() {
            synchronized (SessionWatcher.class) {
                ++sCount;
                mId = sCount;
            }
        }

        public void watchRequest(String url, Map<String, ?> param, Map<String, String> header, String method) {
            final Request request = new Request();
            request.id = mId;
            request.url = url;
            request.method = method;

            for (Map.Entry<String, ?> entry : param.entrySet()) {
                final Object value = entry.getValue();
                request.param.add(new Param(entry.getKey(), value == null ? "null" : value.toString()));
            }

            for (Map.Entry<String, String> entry : header.entrySet()) {
                request.header.add(new Param(entry.getKey(), entry.getValue()));
            }
            addJsonRecord(request);
        }

        public void watchResponse(int statusCode, String msg, Map<String, String> header) {
            final Response response = new Response();
            response.id = mId;
            response.statusCode = statusCode;
            try {
                response.response = new String(msg.getBytes(), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                response.response = msg;
            }
            for (Map.Entry<String, String> entry : header.entrySet()) {
                response.header.add(new Param(entry.getKey(), entry.getValue()));
            }
            addJsonRecord(response);
        }

        public void watchException(Throwable throwable) {
            final Response response = new Response();
            response.id = mId;
            response.exception = Log.getStackTraceString(throwable);
            addJsonRecord(response);
        }
    }

}
