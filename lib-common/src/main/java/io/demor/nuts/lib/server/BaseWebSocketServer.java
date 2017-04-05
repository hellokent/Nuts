package io.demor.nuts.lib.server;

import com.google.common.base.CharMatcher;
import com.google.common.collect.Sets;

import java.io.IOException;
import java.util.Set;

import fi.iki.elonen.NanoWebSocketServer;
import io.demor.nuts.lib.controller.ControllerUtil;
import io.demor.nuts.lib.module.PushObject;

public class BaseWebSocketServer extends NanoWebSocketServer {

    private final Set<NanoWebSocketServer.WebSocket> mAvailableWebSocketList = Sets.newConcurrentHashSet();
    private int mCount = 0;

    public BaseWebSocketServer(int port) {
        super(port);
    }

    @Override
    protected void onClose(final WebSocket webSocket, final WebSocketFrame.CloseCode code, final String reason, final boolean
            initiatedByRemote) {
        synchronized (mAvailableWebSocketList) {
            mAvailableWebSocketList.remove(webSocket);
        }
    }

    @Override
    protected void onException(final WebSocket webSocket, final IOException e) {
        synchronized (mAvailableWebSocketList) {
            mAvailableWebSocketList.remove(webSocket);
        }
    }

    @Override
    protected void onMessage(final WebSocket webSocket, final WebSocketFrame messageFrame) {
        synchronized (mAvailableWebSocketList) {
            mAvailableWebSocketList.add(webSocket);
        }
        final String text = messageFrame.getTextPayload();
        if (CharMatcher.whitespace().matchesAllOf(text)) {
            return;
        }
        ++mCount;
        try {
            webSocket.send(messageFrame.getTextPayload() + mCount);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPong(final WebSocket webSocket, final WebSocketFrame pongFrame) {
    }

    @Override
    public WebSocket openWebSocket(final IHTTPSession handshake) {
        final WebSocket result = super.openWebSocket(handshake);
        synchronized (mAvailableWebSocketList) {
            mAvailableWebSocketList.add(result);
        }
        return result;
    }

    public void sendMessage(final String msg) {
        synchronized (mAvailableWebSocketList) {
            for (NanoWebSocketServer.WebSocket socket : mAvailableWebSocketList) {
                try {
                    socket.send(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void sendPushObj(final PushObject object) {
        sendMessage(ControllerUtil.GSON.toJson(object, PushObject.class));
    }
}
