package com.nuts.sample.ui.controller;

import com.nuts.lib.ToastUtil;
import com.nuts.lib.annotation.viewmapping.OnClick;
import com.nuts.lib.annotation.viewmapping.ViewMapping;
import com.nuts.lib.controller.ControllerCallback;
import com.nuts.lib.controller.ControllerListener;
import com.nuts.lib.log.L;
import com.nuts.sample.R;
import com.nuts.sample.ui.BaseActivity;

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
