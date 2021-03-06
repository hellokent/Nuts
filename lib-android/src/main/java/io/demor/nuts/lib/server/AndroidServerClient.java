package io.demor.nuts.lib.server;

import android.content.Context;
import android.content.res.AssetManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.x5.template.providers.TemplateProvider;

import java.io.IOException;
import java.io.InputStream;

public class AndroidServerClient implements IClient {

    private Context mApplication;
    private AssetManager mAsset;
    private TemplateProvider mTemplateProvider;
    private WifiManager mWifiManager;

    public AndroidServerClient(Context context) {
        mApplication = context;
        mAsset = context.getAssets();
        mTemplateProvider = new AssetTemplateProvider(mAsset);
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    @Override
    public InputStream getResource(String name) throws IOException {
        return mAsset.open(name);
    }

    @Override
    public TemplateProvider getTemplateProvider() {
        return mTemplateProvider;
    }

    @Override
    public String getIpAddress() {
        if (!mWifiManager.isWifiEnabled()) {
            return "";
        }
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();

        if (wifiInfo == null) {
            return "";
        }

        return intToIp(wifiInfo.getIpAddress());
    }

    @Override
    public String getAppId() {
        return mApplication.getPackageName();
    }

    private static String intToIp(int i) {
        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + ((i >> 24) & 0xFF);
    }

}
