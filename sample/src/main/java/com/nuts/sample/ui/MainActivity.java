package com.nuts.sample.ui;

import android.content.Intent;
import android.os.Bundle;

import com.nuts.lib.viewmapping.OnClick;
import com.nuts.lib.viewmapping.ViewMapping;
import com.nuts.sample.R;


@ViewMapping(R.layout.activity_main)
public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            startActivity(new Intent(this, Class.forName("com.nuts.app.MainActivity")));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.controller)
    public void gotoController() {
        //startActivity(JUMPER.viewController().getIntent());
    }

    @OnClick(R.id.jumper)
    public void gotoJumper() {
        JUMPER.viewJumper().startActivity(this);
    }
}
