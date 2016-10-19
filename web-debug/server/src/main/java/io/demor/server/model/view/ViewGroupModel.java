package io.demor.server.model.view;

import android.view.View;
import android.view.ViewGroup;
import com.google.common.collect.Lists;

import java.util.List;

public class ViewGroupModel extends ViewModel {

    public List<ViewModel> children = Lists.newArrayList();

    public int childrenCount;

    public ViewGroupModel(final ViewGroup viewGroup) {
        super(viewGroup);
        childrenCount = viewGroup.getChildCount();
        for (int i = 0; i < childrenCount; ++i) {
            final View child = viewGroup.getChildAt(i);
            children.add(ViewModelFactory.createViewModel(child));
        }
    }
}
