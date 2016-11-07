package io.demor.nuts.lib.eventbus;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.demor.nuts.lib.controller.AppInstance;
import io.demor.nuts.lib.controller.ControllerUtil;
import io.demor.nuts.lib.module.PushObject;
import org.eclipse.jetty.util.BlockingArrayQueue;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketListener;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.eclipse.jetty.websocket.common.WebSocketSession;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public abstract class BaseBarrier implements WebSocketListener, Closeable {

    protected static final BlockingQueue<PushObject> PUSH_QUEUE = new BlockingArrayQueue<>();
    final AppInstance mAppInstance;
    final WebSocketClient mClient;

    public BaseBarrier(final AppInstance appInstance) throws Exception {
        mAppInstance = appInstance;
        mClient = new WebSocketClient();
        mClient.start();
        mClient.connect(this, new URI(String.format("ws://%s:%d", mAppInstance.mHost, mAppInstance.mSocketPort)));
        synchronized (this) {
            wait();
        }
    }

    @Override
    public void onWebSocketBinary(final byte[] payload, final int offset, final int len) {

    }

    @Override
    public void onWebSocketClose(final int statusCode, final String reason) {

    }

    @Override
    public void onWebSocketConnect(final Session session) {
        synchronized (this) {
            notifyAll();
        }
    }

    @Override
    public void onWebSocketError(final Throwable cause) {

    }

    @Override
    public void onWebSocketText(final String message) {
        System.out.println("message:" + message);
        final JsonElement element = new JsonParser().parse(message);
        if (!(element instanceof JsonObject)) {
            return;
        }
        final JsonObject jsonObject = (JsonObject) element;
        final PushObject o = new PushObject();
        o.mType = jsonObject.get("type").getAsInt();
        o.mDataClz = jsonObject.get("dataClz").getAsString();
        try {
            o.mData = ControllerUtil.GSON.fromJson(jsonObject.get("data"), Class.forName(o.mDataClz));
            PUSH_QUEUE.add(o);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void close() throws IOException {
        for (WebSocketSession socketSession : mClient.getOpenSessions()) {
            socketSession.close();
        }
        try {
            mClient.stop();
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    protected void waitForAll(long timeout, TimeUnit unit, PushHandler handler) {
        long startTime = System.currentTimeMillis();
        while ((System.currentTimeMillis() - startTime) < unit.toMillis(timeout)) {
            try {
                final PushObject o = PUSH_QUEUE.poll(unit.toMillis(timeout) - (System.currentTimeMillis() - startTime)
                        , TimeUnit.MILLISECONDS);
                handler.onReceivePush(o);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    protected PushObject waitForSingle(long timeout, TimeUnit unit, PushFilter filter) {
        if (timeout <= 0) {
            return null;
        }
        try {
            long startTime = System.currentTimeMillis();
            PushObject o = PUSH_QUEUE.poll(timeout, unit);
            if (o == null) {
                return null;
            } else if (filter.checkPush(o)) {
                return o;
            } else {
                return waitForSingle(unit.toMillis(timeout) - (System.currentTimeMillis() - startTime), TimeUnit.MILLISECONDS, filter);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }


    interface PushHandler {
        void onReceivePush(PushObject object);
    }

    interface PushFilter {
        boolean checkPush(PushObject object);
    }
}
