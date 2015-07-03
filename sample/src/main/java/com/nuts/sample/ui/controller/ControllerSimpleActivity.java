package com.nuts.sample.ui.controller;

import android.os.Bundle;

import com.nuts.lib.ToastUtil;
import com.nuts.lib.annotation.viewmapping.OnClick;
import com.nuts.lib.annotation.viewmapping.ViewMapping;
import com.nuts.lib.controller.ControllerCallback;
import com.nuts.sample.R;
import com.nuts.sample.ui.BaseActivity;
import com.nuts.sample.utils.Dialogs;

@ViewMapping(R.layout.activity_simple_controller)
public class ControllerSimpleActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @OnClick(R.id.async_run)
    public void asyncRun() {
        TEST_CONTROLLER.run(1).asyncUIWithDialog(new ControllerCallback<String>() {
            @Override
            public void onResult(final String s) {
                ToastUtil.showMessage(s);
            }
        }, Dialogs.createLoadingDialog(this));
    }
}
