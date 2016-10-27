package io.demor.server;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Application;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.*;
import android.os.Handler.Callback;
import android.view.ViewGroup;
import android.view.Window;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.demor.nuts.common.server.Server;
import io.demor.server.api.StateListDrawableInfoApi;
import io.demor.server.api.WidgetApi;
import io.demor.server.res.BitmapDrawableResourceApi;
import io.demor.server.res.DrawableResourceApi;
import io.demor.server.res.ScreenImageResourceApi;
import io.demor.server.res.StateListDrawableResourceApi;
import io.demor.server.sniff.NetworkSniffer;
import io.demor.server.sniff.SimpleSniffer;
import io.demor.server.template.SnifferTemplate;
import io.demor.server.template.WidgetTemplate;
import io.demor.server.template.WsTemplate;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ServerManager {

    public static final Gson GSON = new GsonBuilder()
            .disableHtmlEscaping()
            .create();

    private static final HashMap<String, SimpleSniffer> SNIFFER_MAP = Maps.newHashMap();

    public static Application sApplication;

    static Server sServer;
    static Handler sSnifferHandler;

    static {
        final HandlerThread sniffThread = new HandlerThread("sniff-thread");
        sniffThread.start();
        sSnifferHandler = new Handler(sniffThread.getLooper(), new Callback() {
            @Override
            public boolean handleMessage(final Message msg) {
                final String textMsg = (String) msg.obj;
                sServer.mWebSocketServer.sendMessage(textMsg);
                return true;
            }
        });
    }

    public static void init(Application application) {
        sApplication = application;
        sServer = new Server(sApplication, GSON);
        sServer.mHttpServer.registerApi(new WidgetApi());
        sServer.mHttpServer.registerTemplate("ws", new WsTemplate());
        sServer.mHttpServer.registerTemplate("widget", new WidgetTemplate());
        sServer.mHttpServer.registerTemplate("sniff", new SnifferTemplate());
        sServer.mHttpServer.registerResourceApi("screen.jpg", new ScreenImageResourceApi());

        final Resources res = application.getResources();
        try {
            Class<?> drawableClz = Class.forName(application.getPackageName() + ".R$drawable");
            for (Field field : drawableClz.getDeclaredFields()) {
                field.setAccessible(true);
                final Drawable drawable;
                if (field.getType() != int.class) {
                    continue;
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    drawable = res.getDrawable(field.getInt(drawableClz), application.getTheme());
                } else {
                    drawable = res.getDrawable(field.getInt(drawableClz));
                }
                final String name = field.getName();
                if (drawable instanceof BitmapDrawable) {
                    final BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                    sServer.mHttpServer.registerResourceApi(name + ".jpg", new BitmapDrawableResourceApi(bitmapDrawable.getBitmap()));
                } else if (drawable instanceof StateListDrawable) {
                    final StateListDrawable stateListDrawable = (StateListDrawable) drawable;

                    sServer.mHttpServer.registerResourceApi(name + ".jpg", new StateListDrawableResourceApi(stateListDrawable));
                    sServer.mHttpServer.registerApi(new StateListDrawableInfoApi(name, stateListDrawable));
                } else {
                    sServer.mHttpServer.registerResourceApi(name + ".jpg", new DrawableResourceApi(drawable));
                }
            }
        } catch (ClassNotFoundException | IllegalAccessException e) {
            e.printStackTrace();
        }
        sApplication.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle bundle) {
            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {
                final Window window = activity.getWindow();
                ScreenHelper.setViewReference(((ViewGroup) window.getDecorView()
                        .findViewById(android.R.id.content)).getChildAt(0));
            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });

        ScreenHelper.init(application);
    }

    public static String getHttpHost() {
        return "http://" + getIpAddress() + ":" + getHttpPort();
    }

    public static int getHttpPort() {
        return sServer.getHttpPort();
    }

    public static int getWebSocketPort() {
        return sServer.getWebSocketPort();
    }


    public synchronized static void start(int port) {
        sServer.shutdown();
        sServer.start(port);
    }

    public synchronized static void start() {
        start(0);
    }

    public synchronized static void stop() {
        sServer.shutdown();
    }

    public synchronized static SimpleSniffer getSniffer(String tag) {
        if (SNIFFER_MAP.containsKey(tag)) {
            return SNIFFER_MAP.get(tag);
        }

        final SimpleSniffer sniff = new SimpleSniffer(tag, sSnifferHandler);
        SNIFFER_MAP.put(tag, sniff);
        return sniff;
    }

    public static NetworkSniffer getNetworkSniffer() {
        return new NetworkSniffer(sSnifferHandler);
    }

    public static void showAddressDialog(Context context) {
        final String url = getIpAddress() + ":" + getHttpPort();
        final ClipboardManager clip = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        final AtomicInteger index = new AtomicInteger(0);
        new Builder(context)
                .setTitle("WebDebug地址")
                .setSingleChoiceItems(new String[]{"Api查看器\n" + url + "/web/sniffer",
                        "UI走查\n" + url + "/web/widget"
                }, 0, new OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        index.set(which);
                    }
                })
                .setPositiveButton("存入剪贴版", new OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        switch (index.get()) {
                            case 0:
                                clip.setPrimaryClip(ClipData.newPlainText(null, "http://" + url + "/web/sniffer"));
                                break;
                            case 1:
                            default:
                                clip.setPrimaryClip(ClipData.newPlainText(null, "http://" + url + "/web/widget"));
                                break;
                        }
                    }
                })
                .setNegativeButton("取消", null)
                .create()
                .show();
    }

    public static String getIpAddress() {
        return sServer.getIpAddress();
    }
}
