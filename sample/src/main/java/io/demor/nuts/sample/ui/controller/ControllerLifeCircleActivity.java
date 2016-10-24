package io.demor.nuts.sample.ui.controller;

import io.demor.nuts.lib.ToastUtil;
import io.demor.nuts.lib.annotation.viewmapping.OnClick;
import io.demor.nuts.lib.annotation.viewmapping.ViewMapping;
import io.demor.nuts.lib.controller.ControllerCallback;
import io.demor.nuts.lib.controller.DialogListenerImpl;
import io.demor.nuts.sample.R;
import io.demor.nuts.sample.ui.BaseActivity;
import io.demor.nuts.sample.utils.Dialogs;

@ViewMapping(R.layout.activity_controller_life_circle)
public class ControllerLifeCircleActivity extends BaseActivity {

    @OnClick(R.id.run)
    void runWithLiftCircle() {
        TEST_CONTROLLER.run(1)
                .addListener(new DialogListenerImpl(Dialogs.createLoadingDialog(this)))
                .asyncUI(new ControllerCallback<String>() {
                    @Override
                    public void onResult(final String s) {
                        ToastUtil.showMessage("onResult: " + s);
                    }
                });
    }

}
