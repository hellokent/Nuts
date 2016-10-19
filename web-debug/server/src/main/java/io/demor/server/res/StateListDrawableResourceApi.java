package io.demor.server.res;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import io.demor.nuts.common.server.IResourceApi;
import io.demor.nuts.lib.log.L;
import io.demor.server.Constant;
import org.joor.Reflect;

import java.io.InputStream;
import java.util.Map;

public class StateListDrawableResourceApi implements IResourceApi {

    Drawable[] mDrawableList;

    public StateListDrawableResourceApi(StateListDrawable drawable) {
        mDrawableList = Reflect.on(drawable)
                .field("mDrawableContainerState")
                .field("mDrawables")
                .get();
        L.v("mDrawableList is null:" + (mDrawableList == null));
    }

    @Override
    public InputStream getContent(Map<String, String> param) {
        final Drawable drawable = mDrawableList[Integer.parseInt(param.get("index"))];

        final int width = param.containsKey("width") ? Integer.parseInt("width") :
                (drawable.getIntrinsicWidth() <= 0 ? Constant.DRAWABLE_DEFAULT_WIDTH : drawable.getIntrinsicWidth());

        final int height = param.containsKey("height") ? Integer.parseInt("height") :
                (drawable.getIntrinsicHeight() <= 0 ? Constant.DRAWABLE_DEFAULT_HEIGHT : drawable.getIntrinsicHeight());

        return DrawableResourceApi.getStream(drawable, width, height);
    }

    @Override
    public String mediaType() {
        return "image/jpeg";
    }
}
