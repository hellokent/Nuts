package io.demor.nuts.sample.ui;

import com.nuts.sample.R;
import io.demor.nuts.lib.annotation.viewmapping.OnClick;
import io.demor.nuts.lib.annotation.viewmapping.ViewMapping;


@ViewMapping(R.layout.activity_main)
public class MainActivity extends BaseActivity {

    @OnClick(R.id.controller)
    public void gotoController() {
        JUMPER.viewController()
                .startActivity(this);
    }

    @OnClick(R.id.jumper)
    public void gotoJumper() {
        JUMPER.viewJumper().startActivity(this);
    }
}