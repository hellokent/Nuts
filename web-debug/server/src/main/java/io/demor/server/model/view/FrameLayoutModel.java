package io.demor.server.model.view;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.widget.FrameLayout;

public class FrameLayoutModel extends ViewGroupModel {

    public Integer foreground;

    public FrameLayoutModel(FrameLayout frameLayout) {
        super(frameLayout);
        Drawable foreground = frameLayout.getForeground();
        if (foreground != null && foreground instanceof ColorDrawable) {
            this.foreground = ((ColorDrawable) foreground).getColor();
        }
    }
}
