package com.nuts.sample.ui.jumper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.nuts.lib.ToastUtil;
import com.nuts.lib.viewmapping.OnClick;
import com.nuts.lib.viewmapping.ViewMapping;
import com.nuts.sample.R;
import com.nuts.sample.config.IntentNames;
import com.nuts.sample.ui.BaseActivity;

@ViewMapping(R.layout.activity_jumper)
public class JumperActivity extends BaseActivity{

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            ToastUtil.showMessage("onReceive Broadcast");
        }
    };

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter filter = new IntentFilter();
        filter.addAction("action");
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    @OnClick(R.id.start_activity)
    public void start() {
        JUMPER.viewJumperSimple(2).startActivity(this);
    }

    @OnClick(R.id.start_activity_for_result)
    public void startForResult() {
        JUMPER.viewJumperSimple(3)
                .startActivityForResult(this, 1);
    }

    @OnClick(R.id.send_broadcast)
    public void sendBroadcast() {
        JUMPER.sendBroadcast().sendBroadcast();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                ToastUtil.showMessage(resultCode + "count:" + data.getIntExtra(IntentNames.COUNT, -1));
                break;
        }
    }

}
