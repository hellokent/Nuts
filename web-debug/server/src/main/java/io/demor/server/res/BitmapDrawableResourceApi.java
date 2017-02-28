package io.demor.server.res;

import android.graphics.Bitmap;
import io.demor.nuts.common.server.IResourceApi;
import io.demor.nuts.lib.logger.Logger;
import io.demor.nuts.lib.logger.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map;

public class BitmapDrawableResourceApi implements IResourceApi {

    static final Logger LOGGER = LoggerFactory.getLogger(BitmapDrawableResourceApi.class);
    final Bitmap mBitmap;

    public BitmapDrawableResourceApi(Bitmap bitmap) {
        mBitmap = bitmap;
    }

    public static InputStream getStreamFromBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            LOGGER.v("get stream. empty bitmap");
            return null;
        }
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    @Override
    public InputStream getContent(Map<String, String> param) {
        return getStreamFromBitmap(mBitmap);
    }

    @Override
    public String mediaType() {
        return "image/jpeg";
    }
}
