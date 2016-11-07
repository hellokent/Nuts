package io.demor.nuts.sample.ui.controller;

import io.demor.nuts.lib.ToastUtil;
import io.demor.nuts.lib.annotation.viewmapping.OnClick;
import io.demor.nuts.lib.annotation.viewmapping.ViewMapping;
import io.demor.nuts.lib.controller.ControllerCallback;
import io.demor.nuts.sample.R;
import io.demor.nuts.sample.ui.BaseActivity;

@ViewMapping(R.layout.activity_controller_life_circle)
public class ControllerLifeCircleActivity extends BaseActivity {

    @OnClick(R.id.run)
    void runWithLiftCircle() {
        TEST_CONTROLLER.add(1)
                .addListener(this.<String>createDialogListener())
                .asyncUI(new ControllerCallback<String>() {
                    @Override
                    public void onResult(final String s) {
                        ToastUtil.showMessage("onResult: " + s);
                    }
                });
    }

}
