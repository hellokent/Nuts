package io.demor.nuts.lib.eventbus;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.demor.nuts.lib.controller.AppInstance;
import io.demor.nuts.lib.controller.MethodInfoUtil;
import io.demor.nuts.lib.module.PushObject;
import org.eclipse.jetty.util.BlockingArrayQueue;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketListener;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.eclipse.jetty.websocket.common.WebSocketSession;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public abstract class BaseBarrier implements WebSocketListener, Closeable {

    final AppInstance mAppInstance;
    final String mId;
    final URI mURI;
    protected BlockingArrayQueue<PushObject> mQueue = new BlockingArrayQueue<>();
    protected boolean mStopped = false;
    WebSocketClient mClient;

    public BaseBarrier(final AppInstance appInstance) throws Exception {
        mAppInstance = appInstance;
        mId = getUniqueId();
        mURI = new URI(mAppInstance.getWebSocketUrl() + mId);

        connect();
    }

    @Override
    public void onWebSocketBinary(final byte[] payload, final int offset, final int len) {

    }

    @Override
    public void onWebSocketClose(final int statusCode, final String reason) {
        System.out.println("close " + mStopped);
        if (mStopped) {
            return;
        }
        connect();
    }

    @Override
    public void onWebSocketConnect(final Session session) {
        System.out.println("connect");
    }

    @Override
    public void onWebSocketError(final Throwable cause) {
        if (mStopped) {
            return;
        }
        connect();
    }

    protected void connect() {
        try {
            mClient = new WebSocketClient();
            mClient.start();
            mClient.connect(this, mURI).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onWebSocketText(final String message) {
        System.out.println("onWebSocketText:" + message);
        final JsonElement element = new JsonParser().parse(message);
        if (!(element instanceof JsonObject)) {
            return;
        }
        final JsonObject jsonObject = (JsonObject) element;
        final PushObject o = new PushObject();
        o.mType = jsonObject.get("type").getAsInt();
        o.mDataClz = jsonObject.get("dataClz").getAsString();
        try {
            o.mData = MethodInfoUtil.GSON.fromJson(jsonObject.get("data"), Class.forName(o.mDataClz));
            mQueue.offer(o);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() throws IOException {
        mStopped = true;
        closeSession();
    }

    protected void closeSession() throws IOException {
        System.out.println("close session");
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
        final long millisTimeout = unit.toMillis(timeout);
        while ((System.currentTimeMillis() - startTime) < millisTimeout) {
            try {
                final PushObject o = mQueue.poll(millisTimeout - (System.currentTimeMillis() - startTime)
                        , TimeUnit.MILLISECONDS);
                handler.onReceivePush(o);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    protected PushObject waitForSingle(long timeout, TimeUnit unit, PushFilter filter) {
        if (timeout <= 0) {
            return mQueue.peek();
        }
        try {
            long startTime = System.currentTimeMillis();
            PushObject o = mQueue.poll(timeout, unit);
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

    private String getUniqueId() {
        final Random random = new Random();
        random.setSeed(System.currentTimeMillis() + hashCode());
        return String.valueOf(random.nextLong());
    }

    interface PushHandler {
        void onReceivePush(PushObject object);
    }

    interface PushFilter {
        boolean checkPush(PushObject object);
    }
}
