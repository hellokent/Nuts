package com.nuts.sample.ui;

import com.nuts.lib.viewmapping.OnClick;
import com.nuts.lib.viewmapping.ViewMapping;
import com.nuts.sample.R;


@ViewMapping(R.layout.activity_main)
public class MainActivity extends BaseActivity {

    @OnClick(R.id.controller)
    public void gotoController() {
        startActivity(JUMPER.viewController()
                .getIntent());
    }

    @OnClick(R.id.jumper)
    public void gotoJumper() {
        JUMPER.viewJumper().startActivity(this);
    }
}
