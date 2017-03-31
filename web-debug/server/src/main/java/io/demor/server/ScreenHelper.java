package io.demor.server;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.ViewTreeObserver.OnPreDrawListener;
import io.demor.nuts.lib.log.Logger;
import io.demor.nuts.lib.log.LoggerFactory;
import io.demor.server.model.view.ViewModel;
import io.demor.server.model.view.ViewModelFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

public final class ScreenHelper {

    public static final SparseArray<String> ID_MAP = new SparseArray<>();
    static final Logger LOGGER = LoggerFactory.getLogger(ScreenHelper.class);
    public static int sYOffset;
    public static int sXOffset;
    public static ViewModel sModel;
    static WeakReference<View> sViewReference;
    public static final OnPreDrawListener ON_PRE_DRAW_LISTENER = new OnPreDrawListener() {
        @Override
        public boolean onPreDraw() {
            if (sViewReference.get() != null) {
                sModel = ViewModelFactory.createViewModel(sViewReference.get());
            }
            return true;
        }
    };
    static Bitmap sBitmap;
    static BitmapArrayStream sStream = new BitmapArrayStream();

    static void init(final Application application) {
        try {
            final Class rClz = Class.forName(application.getPackageName() + ".R$id");
            for (Field field : rClz.getDeclaredFields()) {
                try {
                    ID_MAP.put(field.getInt(rClz), field.getName());
                } catch (IllegalArgumentException e) {
                    LOGGER.e("illegal argument:" + field.getName() + ":" + field.getType());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setViewReference(final View view) {
        view.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                final Rect rect = new Rect();
                view.getGlobalVisibleRect(rect);
                sYOffset = rect.top;
                sXOffset = rect.left;
            }
        });

        sViewReference = new WeakReference<>(view);
        view.getViewTreeObserver().addOnPreDrawListener(ON_PRE_DRAW_LISTENER);
    }

    public static byte[] getScreenBytes() throws IOException {
        final View view = sViewReference.get();
        if (view == null) {
            return null;
        }
        long start = System.currentTimeMillis();
        if (sBitmap == null) {
            sBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        }
        final Canvas canvas = new Canvas(sBitmap);
        view.draw(canvas);
        LOGGER.v("get screen: draw:" + (System.currentTimeMillis() - start));
        sBitmap.compress(Bitmap.CompressFormat.JPEG, 100, sStream);
        LOGGER.v("get screen:" + (System.currentTimeMillis() - start));
        sStream.reset();
        return sStream.getByteArray();
    }

    public static View getView() {
        return sViewReference.get();
    }


    static class BitmapArrayStream extends ByteArrayOutputStream {

        public BitmapArrayStream() {
        }

        public byte[] getByteArray() {
            return buf;
        }

    }
}
