package io.demor.server.model.view;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

public class ImageViewModel extends ViewModel {

    public int imageWidth;
    public int imageHeight;
    public String scaleType;

    public ImageViewModel(ImageView view) {
        super(view);

        final Drawable drawable = view.getDrawable();
        if (drawable != null) {
            imageWidth = drawable.getIntrinsicWidth();
            imageHeight = drawable.getIntrinsicHeight();
        }

        scaleType = view.getScaleType().name();
    }
}
