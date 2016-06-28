package io.demor.nuts.test;

import com.google.common.collect.Lists;
import fi.iki.elonen.NanoWebSocketServer;

import java.io.IOException;
import java.util.List;

public class WebSocketServer extends NanoWebSocketServer {

    public List<WebSocket> mSockets = Lists.newArrayList();
    public void sendText

    public WebSocketServer() {
        super(0);
    }

    @Override
    protected void onClose(WebSocket webSocket, WebSocketFrame.CloseCode code, String reason, boolean initiatedByRemote) {
        mSockets.remove(webSocket);
    }

    @Override
    protected void onException(WebSocket webSocket, IOException e) {
        mSockets.remove(webSocket);
    }

    @Override
    protected void onMessage(WebSocket webSocket, WebSocketFrame messageFrame) {

    }

    @Override
    protected void onPong(WebSocket webSocket, WebSocketFrame pongFrame) {
        mSockets.add(webSocket);
    }

}
