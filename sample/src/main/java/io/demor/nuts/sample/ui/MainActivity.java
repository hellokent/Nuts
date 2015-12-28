package io.demor.nuts.sample.ui;

import android.os.Bundle;
import io.demor.nuts.lib.annotation.log.MethodLog;
import io.demor.nuts.lib.annotation.viewmapping.OnClick;
import io.demor.nuts.lib.annotation.viewmapping.ViewMapping;
import io.demor.nuts.lib.controller.ControllerCallback;
import io.demor.nuts.sample.R;
import io.demor.nuts.sample.controller.TestController2;


@ViewMapping(R.layout.activity_main)
public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        method();
        new TestController2().test2().asyncUI(new ControllerCallback<Void>() {
            @Override
            public void onResult(Void aVoid) {
                System.out.println("test2 return");
            }
        });
    }

    @MethodLog
    private void method() {
        System.out.println("~~~~~~~");
    }

    @OnClick(R.id.controller)
    public void gotoController() {
        method();
        JUMPER.viewController()
                .startActivity(this);
    }

    @OnClick(R.id.jumper)
    public void gotoJumper() {
        method();
        JUMPER.viewJumper().startActivity(this);
    }
}