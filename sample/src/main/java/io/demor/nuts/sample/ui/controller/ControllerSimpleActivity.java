package io.demor.nuts.sample.ui.controller;

import android.os.Bundle;

import com.nuts.sample.R;
import io.demor.nuts.lib.ToastUtil;
import io.demor.nuts.lib.annotation.viewmapping.OnClick;
import io.demor.nuts.lib.annotation.viewmapping.ViewMapping;
import io.demor.nuts.lib.controller.ControllerCallback;
import io.demor.nuts.sample.ui.BaseActivity;
import io.demor.nuts.sample.utils.Dialogs;

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