package io.demor.nuts.sample.ui;

import android.os.Bundle;
import android.widget.TextView;
import io.demor.nuts.lib.NutsApplication;
import io.demor.nuts.lib.annotation.viewmapping.OnClick;
import io.demor.nuts.lib.annotation.viewmapping.ViewMapping;
import io.demor.nuts.sample.R;
import io.demor.server.WebDebug;


@ViewMapping(R.layout.activity_main)
public class MainActivity extends BaseActivity {

    @ViewMapping(R.id.ip_address)
    TextView mIpAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WebDebug.start();
        mIpAddress.setText(String.format(
                "http://%s:%d\n" +
                        "ws://%s:%d\n" +
                        "WebDebug:http://%s:%d/web/widget",
                NutsApplication.getIpAddress(), NutsApplication.getHttpPort(),
                NutsApplication.getIpAddress(), NutsApplication.getWebSocketPort(),
                NutsApplication.getIpAddress(), WebDebug.getHttpPort()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        WebDebug.stop();
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

    @OnClick(R.id.show_web_debug)
    public void showWebDebug() {
        WebDebug.showAddressDialog(this);
    }

    @OnClick(R.id.net_watcher)
    public void gotoNetWatcher() {
        JUMPER.viewNetWatcher().startActivity(this);
    }
}