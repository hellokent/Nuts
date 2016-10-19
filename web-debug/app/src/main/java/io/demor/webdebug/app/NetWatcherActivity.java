package io.demor.webdebug.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import com.google.common.collect.Maps;
import io.demor.server.ServerManager;
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
        mSniffer = ServerManager.getNetworkSniffer();
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
                    mWatcher.watchResponse(200, "{\"code\":0," +
                            "\"list\":[{\"id\":\"9505\"," +
                            "\"title\":\"\\u3010\\u5317\\u4eac\\u3011kk\\u62fc\\u8f66\\u5e02\\u5185\\u7ebf\\u8def" +
                            "\\u7ee7\\u7eed\\u514d\\u5355\\uff01\",\"type\":\"0\",\"booking_push_datetime\":\"05-27 " +
                            "18:10\",\"audio_url\":\"\",\"audio_second\":\"0\\u2033\",\"content\":\"\"," +
                            "\"category\":\"\\u901a\\u77e5\",\"read\":1,\"priority\":\"1\"},{\"id\":\"9493\"," +
                            "\"title\":\"\\u3010\\u603b\\u90e8\\u3011\\u5e08\\u5085\\u6765\\u652f\\u62db\\uff0c" +
                            "\\u7687\\u51a0\\u8be5\\u5982\\u4f55\\u4f7f\\u7528\\u624d\\u80fd\\u6700\\u9ad8\\u6548" +
                            "\\uff1f\",\"type\":\"0\",\"booking_push_datetime\":\"05-27 16:30\",\"audio_url\":\"\"," +
                            "\"audio_second\":\"0\\u2033\",\"content\":\"\",\"category\":\"\\u901a\\u77e5\"," +
                            "\"read\":1,\"priority\":\"0\"},{\"id\":\"9486\"," +
                            "\"title\":\"\\u3010\\u5317\\u4eac\\u3011\\uff08\\u91cd\\u8981\\uff09\\u5317\\u4eac" +
                            "\\u5206\\u516c\\u53f8\\u529e\\u516c\\u5730\\u5740\\u53d8\\u66f4\\u901a\\u77e5\"," +
                            "\"type\":\"0\",\"booking_push_datetime\":\"05-27 14:42\",\"audio_url\":\"\"," +
                            "\"audio_second\":\"0\\u2033\",\"content\":\"\",\"category\":\"\\u901a\\u77e5\"," +
                            "\"read\":1,\"priority\":\"0\"},{\"id\":\"9458\"," +
                            "\"title\":\"\\u3010\\u5317\\u4eac\\u3011KK\\u62fc\\u8f66\\uff0c\\u4eca\\u65e5\\u5e02" +
                            "\\u5185\\u7ebf\\u8def\\u4e0a\\u7ebf\\uff0c\\u65b0\\u7ebf\\u8def\\u5168\\u90e8\\u514d" +
                            "\\u5355\\u5566\\uff01\",\"type\":\"0\",\"booking_push_datetime\":\"05-26 18:00\"," +
                            "\"audio_url\":\"\",\"audio_second\":\"0\\u2033\",\"content\":\"\"," +
                            "\"category\":\"\\u901a\\u77e5\",\"read\":1,\"priority\":\"1\"},{\"id\":\"9428\"," +
                            "\"title\":\"e\\u4ee3\\u9a7e\\u5546\\u57ce\\u4e0a\\u7ebf\\u5566\\uff01\\uff01\\uff01\"," +
                            "\"type\":\"0\",\"booking_push_datetime\":\"05-25 17:28\",\"audio_url\":\"\"," +
                            "\"audio_second\":\"0\\u2033\",\"content\":\"\",\"category\":\"\\u901a\\u77e5\"," +
                            "\"read\":1,\"priority\":\"0\"},{\"id\":\"9372\"," +
                            "\"title\":\"\\u3010\\u5317\\u4eac\\u3011\\u7b2c\\u4e8c\\u6279\\u9886\\u53d6\\u80f8" +
                            "\\u5361\\u540d\\u5355\",\"type\":\"0\",\"booking_push_datetime\":\"05-24 10:38\"," +
                            "\"audio_url\":\"\",\"audio_second\":\"0\\u2033\",\"content\":\"\"," +
                            "\"category\":\"\\u901a\\u77e5\",\"read\":1,\"priority\":\"0\"},{\"id\":\"9288\"," +
                            "\"title\":\"\\u3010\\u603b\\u90e8\\u3011\\u63a5\\u5355\\u5176\\u5b9e\\u6ca1\\u6709" +
                            "\\u90a3\\u4e48\\u96be\\uff0c\\u60a8\\u77e5\\u9053\\u5417\\uff1f\",\"type\":\"0\"," +
                            "\"booking_push_datetime\":\"05-23 08:00\",\"audio_url\":\"\"," +
                            "\"audio_second\":\"0\\u2033\",\"content\":\"\",\"category\":\"\\u901a\\u77e5\"," +
                            "\"read\":1,\"priority\":\"0\"},{\"id\":\"9322\"," +
                            "\"title\":\"\\u3010\\u5317\\u4eac\\u3011\\uff08\\u91cd\\u8981\\uff09\\u624b\\u673a" +
                            "\\u652f\\u67b6\\u505c\\u4f7f\\u901a\\u77e5\",\"type\":\"0\"," +
                            "\"booking_push_datetime\":\"05-22 22:34\",\"audio_url\":\"\"," +
                            "\"audio_second\":\"0\\u2033\",\"content\":\"\",\"category\":\"\\u89c4\\u5219\"," +
                            "\"read\":1,\"priority\":\"0\"},{\"id\":\"9233\"," +
                            "\"title\":\"\\u3010\\u5317\\u4eac\\u3011\\uff08\\u91cd\\u8981\\uff09\\u5de5\\u4f53" +
                            "\\u65b0\\u79e9\\u5e8f\",\"type\":\"0\",\"booking_push_datetime\":\"05-21 16:36\"," +
                            "\"audio_url\":\"\",\"audio_second\":\"0\\u2033\",\"content\":\"\"," +
                            "\"category\":\"\\u901a\\u77e5\",\"read\":1,\"priority\":\"0\"},{\"id\":\"7874\"," +
                            "\"title\":\"\\u3010\\u5317\\u4eac\\u3011\\u597d\\u6d88\\u606f  " +
                            "\\u7535\\u4fe1\\u5957\\u9910\\u964d\\u4ef7\\u5347\\u7ea7\",\"type\":\"0\"," +
                            "\"booking_push_datetime\":\"04-02 16:55\",\"audio_url\":\"\"," +
                            "\"audio_second\":\"0\\u2033\",\"content\":\"\",\"category\":\"\\u901a\\u77e5\"," +
                            "\"read\":1,\"priority\":\"0\"}],\"message\":\"\\u8bfb\\u53d6\\u6210\\u529f\"}\n", SIMPLE_RESPONSE_HEADER);
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
