package io.demor.nuts.lib.api;

import com.google.common.collect.Lists;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class WebSocketServer extends NanoWebSocketServer {

    public List<WebSocket> mSockets = Lists.newArrayList();
    public WebSocketServer() {
        super(0);
    }

    public void sendText(final String text) {

        for (Iterator<WebSocket> i = mSockets.iterator(); i.hasNext(); ) {
            final WebSocket s = i.next();
            try {
                s.send(text);
            } catch (IOException e) {
                e.printStackTrace();
                i.remove();
            }
        }
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
