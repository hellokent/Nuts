package io.demor.server.sniff;

import android.os.Handler;
import io.demor.server.ServerManager;

import java.util.List;
import java.util.Vector;

public class SimpleSniffer {

    final String mTag;
    final Handler mHandler;
    final List<String> mHistoryMsgList = new Vector<>();

    public SimpleSniffer(String tag, Handler handler) {
        mTag = tag;
        mHandler = handler;
    }

    public void addRecord(final String record) {
        mHandler.obtainMessage(0, record).sendToTarget();
        mHistoryMsgList.add(record);
    }

    public void addJsonRecord(Object obj) {
        addRecord(ServerManager.GSON.toJson(obj));
    }

    public String getTag() {
        return mTag;
    }
}
