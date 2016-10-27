package io.demor.nuts.sample.ui;

import android.os.Bundle;
import android.widget.TextView;
import io.demor.nuts.lib.NutsApplication;
import io.demor.nuts.lib.annotation.viewmapping.OnClick;
import io.demor.nuts.lib.annotation.viewmapping.ViewMapping;
import io.demor.nuts.sample.R;


@ViewMapping(R.layout.activity_main)
public class MainActivity extends BaseActivity {

    @ViewMapping(R.id.ip_address)
    TextView mIpAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIpAddress.setText(NutsApplication.getIpAddress() + ":" + NutsApplication.getListerningPort());
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