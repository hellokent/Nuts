package io.demor.server.model.view;

import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

public final class ViewModelFactory {

    public static ViewModel createViewModel(final View view) {
        if (view instanceof LinearLayout) {
            return new LinearLayoutModel((LinearLayout) view);
        } else if (view instanceof FrameLayout) {
            return new FrameLayoutModel((FrameLayout) view);
        } else if (view instanceof TextView) {
            return new TextViewModel((TextView) view);
        } else if (view instanceof ImageView) {
            return new ImageViewModel((ImageView) view);
        } else if (view instanceof ListView) {
            return new ListViewModel((ListView) view);
        } else if (view instanceof ViewGroup) {
            return new ViewGroupModel((ViewGroup) view);
        }
        return new ViewModel(view);
    }
}
