package com.nuts.sample.ui.controller;

import com.nuts.lib.ToastUtil;
import com.nuts.lib.annotation.viewmapping.OnClick;
import com.nuts.lib.annotation.viewmapping.ViewMapping;
import com.nuts.lib.controller.ControllerCallback;
import com.nuts.sample.R;
import com.nuts.sample.controller.DemoException;
import com.nuts.sample.ui.BaseActivity;
import com.nuts.sample.utils.Dialogs;

@ViewMapping(R.layout.activity_controller)
public class ControllerActivity extends BaseActivity {

    @OnClick(R.id.simple)
    public void gotoSimple(){
        JUMPER.simpleController().startActivity(this);
    }

    @OnClick(R.id.check_activity)
    public void gotoCheckActivity() {
        JUMPER.checkActivityController().startActivity(this);
    }

    @OnClick(R.id.life_circle)
    public void lifeCircle() {
        JUMPER.viewControllerLifeCircle()
                .startActivity(this);
    }

    @OnClick(R.id.run_exception)
    public void runWithException() {
        TEST_CONTROLLER.runWithException()
                .asyncUIWithDialog(new ControllerCallback<Void>() {
                    @Override
                    public void onResult(final Void aVoid) {
                        ToastUtil.showMessage("onResult");
                    }

                    @Override
                    public void handleException(final Exception e) {
                        super.handleException(e);
                        if (e instanceof DemoException) {
                            ToastUtil.showMessage(((DemoException) e).getMsg());
                        }
                    }
                }, Dialogs.createLoadingDialog(this));
    }
}
