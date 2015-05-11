package com.nuts.sample.ui.controller;

import com.nuts.lib.viewmapping.OnClick;
import com.nuts.lib.viewmapping.ViewMapping;
import com.nuts.sample.R;
import com.nuts.sample.ui.BaseActivity;

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
}
