package io.demor.nuts.sample.ui;

import android.os.Bundle;
import io.demor.nuts.lib.annotation.viewmapping.OnClick;
import io.demor.nuts.lib.annotation.viewmapping.ViewMapping;
import io.demor.nuts.sample.R;


@ViewMapping(R.layout.activity_main)
public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

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