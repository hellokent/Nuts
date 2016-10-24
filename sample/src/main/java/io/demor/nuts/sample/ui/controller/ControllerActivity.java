package io.demor.nuts.sample.ui.controller;

import io.demor.nuts.lib.ToastUtil;
import io.demor.nuts.lib.annotation.viewmapping.OnClick;
import io.demor.nuts.lib.annotation.viewmapping.ViewMapping;
import io.demor.nuts.lib.controller.ControllerCallback;
import io.demor.nuts.lib.controller.DialogListenerImpl;
import io.demor.nuts.sample.R;
import io.demor.nuts.sample.controller.DemoException;
import io.demor.nuts.sample.ui.BaseActivity;
import io.demor.nuts.sample.utils.Dialogs;

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
                .addListener(new DialogListenerImpl(Dialogs.createLoadingDialog(this)))
                .asyncUI(new ControllerCallback<Void>() {
                    @Override
                    public void onResult(final Void aVoid) {
                        ToastUtil.showMessage("onResult");
                    }

                    @Override
                    public void onException(final Throwable e) {
                        super.onException(e);
                        if (e instanceof DemoException) {
                            ToastUtil.showMessage(((DemoException) e).getMsg());
                        }
                    }
                });
    }
}
