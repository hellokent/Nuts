package io.demor.nuts.lib.server;

import com.google.common.base.CharMatcher;
import com.google.common.collect.Sets;
import fi.iki.elonen.NanoWebSocketServer;
import io.demor.nuts.lib.logger.Logger;
import io.demor.nuts.lib.logger.LoggerFactory;

import java.io.IOException;
import java.util.Set;

public class BaseWebSocketServer extends NanoWebSocketServer {

    static final Logger LOGGER = LoggerFactory.getLogger(BaseWebSocketServer.class);
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
        LOGGER.v("onClose");
    }

    @Override
    protected void onException(final WebSocket webSocket, final IOException e) {
        synchronized (mAvailableWebSocketList) {
            mAvailableWebSocketList.remove(webSocket);
        }
        LOGGER.exception(e);
    }

    @Override
    protected void onMessage(final WebSocket webSocket, final WebSocketFrame messageFrame) {
        synchronized (mAvailableWebSocketList) {
            mAvailableWebSocketList.add(webSocket);
        }
        final String text = messageFrame.getTextPayload();
        LOGGER.v("onMessage:%s", text);
        if (CharMatcher.WHITESPACE.matchesAllOf(text)) {
            return;
        }
        ++mCount;
        try {
            webSocket.send(messageFrame.getTextPayload() + mCount);
        } catch (IOException e) {
            LOGGER.exception(e);
        }
    }

    @Override
    protected void onPong(final WebSocket webSocket, final WebSocketFrame pongFrame) {
        LOGGER.v("onPong");
    }

    @Override
    public WebSocket openWebSocket(final IHTTPSession handshake) {
        final WebSocket result = super.openWebSocket(handshake);
        LOGGER.v("open web socket, uri:%s", handshake.getUri());
        synchronized (mAvailableWebSocketList) {
            mAvailableWebSocketList.add(result);
        }
        return result;
    }

    public void sendMessage(final String msg) {
        synchronized (mAvailableWebSocketList) {
            for (NanoWebSocketServer.WebSocket socket : mAvailableWebSocketList) {
                try {
                    LOGGER.v("web socket, send:%s", msg);
                    socket.send(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
