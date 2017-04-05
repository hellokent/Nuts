package io.demor.nuts.lib.server.impl;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import com.google.common.base.Strings;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import io.demor.nuts.lib.server.BaseWebSocketServer;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ApiWebSocketServer extends BaseWebSocketServer {

    private final Multimap<String, String> mFailedMessage = MultimapBuilder.hashKeys().linkedListValues().build();
    private final Handler mTimeoutHandler;

    ApiWebSocketServer(final int port) {
        super(port);
        final HandlerThread thread = new HandlerThread("websocket-timeout");
        thread.start();
        mTimeoutHandler = new Handler(thread.getLooper()) {
            @Override
            public void handleMessage(final Message msg) {
                super.handleMessage(msg);
                mFailedMessage.removeAll(msg.obj.toString());
            }
        };
    }

    @Override
    protected void onClose(final WebSocket webSocket, final WebSocketFrame.CloseCode code, final String reason, final boolean initiatedByRemote) {
        super.onClose(webSocket, code, reason, initiatedByRemote);
        sendCleanEvent(webSocket.getHandshakeRequest().getUri());
    }

    @Override
    protected void onException(final WebSocket webSocket, final IOException e) {
        super.onException(webSocket, e);
        sendCleanEvent(webSocket.getHandshakeRequest().getUri());
    }

    protected void sendCleanEvent(final String uri) {
        mFailedMessage.put(uri, "");
        mTimeoutHandler.sendMessageAtTime(mTimeoutHandler.obtainMessage(0, uri), TimeUnit.MINUTES.toMillis(10));
    }

    @Override
    public WebSocket openWebSocket(final IHTTPSession handshake) {
        final String key = handshake.getUri();
        mTimeoutHandler.removeMessages(0, key);
        final WebSocket socket = super.openWebSocket(handshake);
        if (mFailedMessage.containsKey(key)) {
            for (String msg : mFailedMessage.get(key)) {
                if (Strings.isNullOrEmpty(msg)) {
                    continue;
                }
                try {
                    socket.send(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            mFailedMessage.removeAll(key);
        }
        return socket;
    }


    @Override
    public void sendMessage(final String msg) {
        super.sendMessage(msg);
        for (String key : mFailedMessage.keySet()) {
            mFailedMessage.put(key, msg);
        }
    }
}
