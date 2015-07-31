package com.nuts.sample.ui.controller;

import com.nuts.lib.ToastUtil;
import com.nuts.lib.annotation.viewmapping.OnClick;
import com.nuts.lib.annotation.viewmapping.ViewMapping;
import com.nuts.lib.controller.ControllerCallback;
import com.nuts.sample.R;
import com.nuts.sample.ui.BaseActivity;

@ViewMapping(R.layout.activity_check_activity)
public class ControllerCheckActivityActivity extends BaseActivity {

    @OnClick(R.id.async_run)
    void run() {
        TEST_CONTROLLER.runCheckActivity().asyncUI(new ControllerCallback<Void>() {
            @Override
            public void onResult(final Void aVoid) {
                ToastUtil.showMessage("on Result");
            }
        });
    }

    @OnClick(R.id.async_run_finish)
    void runAndFinish() {
        TEST_CONTROLLER.run(1)
                .setNeedCheckActivity(true)
                .asyncUI(new ControllerCallback<String>() {
                    @Override
                    public void onResult(final String s) {
                        ToastUtil.showMessage("onResult");
                    }
                });
        finish();
    }
}
