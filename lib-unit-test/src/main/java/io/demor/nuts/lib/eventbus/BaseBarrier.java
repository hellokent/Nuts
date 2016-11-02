package io.demor.nuts.lib.eventbus;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.demor.nuts.lib.controller.AppInstance;
import io.demor.nuts.lib.controller.ControllerUtil;
import io.demor.nuts.lib.module.PushObject;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketListener;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import java.net.URI;

public abstract class BaseBarrier implements WebSocketListener {

    final AppInstance mAppInstance;

    public BaseBarrier(final AppInstance appInstance) throws Exception {
        mAppInstance = appInstance;
        final WebSocketClient client = new WebSocketClient();
        client.start();
        client.connect(this, new URI(String.format("ws://%s:%d", mAppInstance.mHost, mAppInstance.mSocketPort)));
    }

    @Override
    public void onWebSocketBinary(final byte[] payload, final int offset, final int len) {

    }

    @Override
    public void onWebSocketClose(final int statusCode, final String reason) {

    }

    @Override
    public void onWebSocketConnect(final Session session) {

    }

    @Override
    public void onWebSocketError(final Throwable cause) {

    }

    @Override
    public void onWebSocketText(final String message) {
        final JsonElement element = new JsonParser().parse(message);
        if (!(element instanceof JsonObject)) {
            return;
        }
        System.out.println("message:" + message);
        final JsonObject jsonObject = (JsonObject) element;
        final PushObject o = new PushObject();
        o.mType = jsonObject.get("type").getAsInt();
        o.mDataClz = jsonObject.get("dataClz").getAsString();
        try {
            o.mData = ControllerUtil.GSON.fromJson(jsonObject.getAsJsonObject("data"), Class.forName(o.mDataClz));
            onReceiveData(o);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    protected abstract void onReceiveData(final PushObject object);
}
