package io.demor.nuts.sample.ui;

import android.app.Activity;
import android.os.Bundle;
import io.demor.nuts.lib.controller.DialogListenerImpl;
import io.demor.nuts.lib.viewmapping.ClickMapping;
import io.demor.nuts.lib.viewmapping.ViewMapUtil;
import io.demor.nuts.sample.config.Const;
import io.demor.nuts.sample.utils.Dialogs;

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

    public <T> DialogListenerImpl<T> createDialogListener() {
        return new DialogListenerImpl<>(Dialogs.createLoadingDialog(this));
    }
}
