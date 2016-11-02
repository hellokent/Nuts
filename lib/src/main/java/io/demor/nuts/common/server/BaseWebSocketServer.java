package io.demor.nuts.common.server;

import com.google.common.base.CharMatcher;
import com.google.common.collect.Sets;
import fi.iki.elonen.NanoWebSocketServer;
import io.demor.nuts.lib.log.L;

import java.io.IOException;
import java.util.Set;

public class BaseWebSocketServer extends NanoWebSocketServer {

    final Set<NanoWebSocketServer.WebSocket> mAvailableWebSocketList = Sets.newConcurrentHashSet();
    int mCount = 0;

    public BaseWebSocketServer(int port) {
        super(port);
    }

    @Override
    protected void onClose(final WebSocket webSocket, final WebSocketFrame.CloseCode code, final String reason, final boolean
            initiatedByRemote) {
        synchronized (mAvailableWebSocketList) {
            mAvailableWebSocketList.remove(webSocket);
        }
        L.v("onClose");
    }

    @Override
    protected void onException(final WebSocket webSocket, final IOException e) {
        synchronized (mAvailableWebSocketList) {
            mAvailableWebSocketList.remove(webSocket);
        }
        L.exception(e);
    }

    @Override
    protected void onMessage(final WebSocket webSocket, final WebSocketFrame messageFrame) {
        synchronized (mAvailableWebSocketList) {
            mAvailableWebSocketList.add(webSocket);
        }
        final String text = messageFrame.getTextPayload();
        L.v("onMessage:%s", text);
        if (CharMatcher.WHITESPACE.matchesAllOf(text)) {
            return;
        }
        ++mCount;
        try {
            webSocket.send(messageFrame.getTextPayload() + mCount);
        } catch (IOException e) {
            L.exception(e);
        }
    }

    @Override
    protected void onPong(final WebSocket webSocket, final WebSocketFrame pongFrame) {
        L.v("onPong");
    }

    @Override
    public WebSocket openWebSocket(final IHTTPSession handshake) {
        final WebSocket result = super.openWebSocket(handshake);
        L.v("open web socket, uri:%s", handshake.getUri());
        synchronized (mAvailableWebSocketList) {
            mAvailableWebSocketList.add(result);
        }
        return result;
    }

    public void sendMessage(final String msg) {
        synchronized (mAvailableWebSocketList) {
            for (NanoWebSocketServer.WebSocket socket : mAvailableWebSocketList) {
                try {
                    L.v("web socket, send:%s", msg);
                    socket.send(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
