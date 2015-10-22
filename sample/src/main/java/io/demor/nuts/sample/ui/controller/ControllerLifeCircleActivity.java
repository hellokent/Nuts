package io.demor.nuts.sample.ui.controller;

import io.demor.nuts.lib.ToastUtil;
import io.demor.nuts.lib.annotation.viewmapping.OnClick;
import io.demor.nuts.lib.annotation.viewmapping.ViewMapping;
import io.demor.nuts.lib.controller.ControllerCallback;
import io.demor.nuts.lib.controller.ControllerListener;
import io.demor.nuts.lib.log.L;
import io.demor.nuts.sample.R;
import io.demor.nuts.sample.ui.BaseActivity;

@ViewMapping(R.layout.activity_controller_life_circle)
public class ControllerLifeCircleActivity extends BaseActivity {

    @OnClick(R.id.run)
    void runWithLiftCircle() {
        TEST_CONTROLLER.run(1)
                .addListener(new ControllerListener<String>() {
                    @Override
                    public void onBegin() {
                        L.v("onBegin");
                    }

                    @Override
                    public void onEnd(final String response) {
                        L.v("onEnd:%s", response);
                    }

                    @Override
                    public void onException(final Throwable throwable) {
                        ToastUtil.showMessage("onException");
                    }
                })
                .asyncUI(new ControllerCallback<String>() {
                    @Override
                    public void onResult(final String s) {
                        ToastUtil.showMessage("onResult: " + s);
                    }
                });
    }

}
