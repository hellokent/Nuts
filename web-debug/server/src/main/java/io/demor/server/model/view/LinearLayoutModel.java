package io.demor.server.model.view;

import android.widget.LinearLayout;

public class LinearLayoutModel extends ViewGroupModel {

    public String orientation;
    public float weightSum;

    public LinearLayoutModel(LinearLayout linearLayout) {
        super(linearLayout);
        int orientationInt = linearLayout.getOrientation();

        orientation = orientationInt == LinearLayout.HORIZONTAL ? "HORIZONTAL" : "VERTICAL";
        weightSum = linearLayout.getWeightSum();
    }
}
