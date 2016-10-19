package io.demor.server.res;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import io.demor.nuts.common.server.IResourceApi;
import io.demor.server.Constant;

import java.io.InputStream;
import java.util.Map;

public class DrawableResourceApi implements IResourceApi {

    Drawable mDrawable;

    int mWidth;
    int mHeight;

    public DrawableResourceApi(final Drawable drawable) {
        mDrawable = drawable;
        mWidth = mDrawable.getIntrinsicWidth() <= 0 ? Constant.DRAWABLE_DEFAULT_WIDTH : mDrawable.getIntrinsicWidth();
        mHeight = mDrawable.getIntrinsicHeight() <= 0 ? Constant.DRAWABLE_DEFAULT_HEIGHT : mDrawable.getIntrinsicHeight();
    }

    public static InputStream getStream(Drawable drawable, int width, int height) {
        final Bitmap bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        final InputStream stream = BitmapDrawableResourceApi.getStreamFromBitmap(bitmap);
        bitmap.recycle();
        return stream;
    }

    @Override
    public InputStream getContent(final Map<String, String> param) {
        if (param.containsKey("width")) {
            mWidth = Integer.parseInt(param.get("width"));
        }
        if (param.containsKey("height")) {
            mHeight = Integer.parseInt(param.get("height"));
        }

        return getStream(mDrawable, mWidth, mHeight);
    }

    @Override
    public String mediaType() {
        return "image/jpeg";
    }
}
