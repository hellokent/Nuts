package io.demor.nuts.sample.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import com.google.common.collect.Maps;
import io.demor.nuts.sample.R;
import io.demor.server.WebDebug;
import io.demor.server.sniff.NetworkSniffer;

import java.util.Map;


public class NetWatcherActivity extends Activity implements OnClickListener {

    static final Map<String, String> SIMPLE_REQUEST_PARAM = Maps.newHashMap();
    static final Map<String, String> SIMPLE_REQUEST_HEADER = Maps.newHashMap();
    static final Map<String, String> SIMPLE_RESPONSE_HEADER = Maps.newHashMap();

    static {
        SIMPLE_REQUEST_PARAM.put("app_ver", "3.0.0.0");
        SIMPLE_REQUEST_PARAM.put("method", "driver.msg.list");
        SIMPLE_REQUEST_PARAM.put("model", "samsung-SCH-I959");

        SIMPLE_REQUEST_HEADER.put("req_header1", "header_value1");
        SIMPLE_REQUEST_HEADER.put("req_header2", "header_value2");

        SIMPLE_RESPONSE_HEADER.put("resp_header1", "req1");
        SIMPLE_RESPONSE_HEADER.put("resp_header2", "req2");
    }


    NetworkSniffer mSniffer;
    NetworkSniffer.SessionWatcher mWatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_watcher);
        findViewById(R.id.request).setOnClickListener(this);
        findViewById(R.id.response).setOnClickListener(this);
        findViewById(R.id.exception).setOnClickListener(this);
        mSniffer = WebDebug.getNetworkSniffer();
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.request:
                mWatcher = mSniffer.createWatcher();
                mWatcher.watchRequest("http://api.edaijia.cn/rest", SIMPLE_REQUEST_PARAM, SIMPLE_REQUEST_HEADER, "GET");
                break;
            case R.id.response:
                if (mWatcher == null) {
                    return;
                } else {
                    mWatcher.watchResponse(200, "{\"code\": 0,\"message\": \"OK\"}", SIMPLE_RESPONSE_HEADER);
                    mWatcher = null;
                }
                break;
            case R.id.exception:
                if (mWatcher == null) {
                    return;
                } else {
                    mWatcher.watchException(new Throwable());
                    mWatcher = null;
                }
                break;
        }

    }
}
