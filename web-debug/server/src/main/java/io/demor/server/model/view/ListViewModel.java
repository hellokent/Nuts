package io.demor.server.model.view;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ListView;

public class ListViewModel extends ViewGroupModel {

    int dividerColor;
    int dividerHeight;

    public ListViewModel(ListView listView) {
        super(listView);

        final Drawable divider = listView.getDivider();
        if (divider instanceof ColorDrawable) {
            dividerColor = ((ColorDrawable) divider).getColor();
        }
        dividerHeight = listView.getDividerHeight();
    }
}
