package com.nuts.sample.ui;

import android.app.Activity;
import android.os.Bundle;

import com.nuts.lib.viewmapping.ClickMapping;
import com.nuts.lib.viewmapping.ViewMapUtil;
import com.nuts.sample.config.Const;

public class BaseActivity extends Activity implements Const {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BUS.register(this);
        setContentView(ViewMapUtil.map(this));
        ClickMapping.map(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        BUS.unregister(this);
    }
}
