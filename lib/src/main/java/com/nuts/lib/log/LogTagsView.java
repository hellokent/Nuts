package com.nuts.lib.log;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.google.common.collect.Lists;
import com.nuts.lib.BaseApplication;
import com.nuts.lib.R;
import static com.nuts.lib.Globals.UI_HANDLER;

public class LogTagsView {

    final static SimpleDateFormat FORMAT = new SimpleDateFormat("HH:mm:ss");

    TextView mLogText;
    List<String> mLogs = Lists.newLinkedList();
    LogCleanerRunnable mCleanerRunnable;

    public LogTagsView() {
        final Context context = BaseApplication.getGlobalContext();
        mLogText = (TextView) LayoutInflater.from(context).inflate(R.layout.log_view, null);
        showLog();
    }

    public View getRoot() {
        return mLogText;
    }

    void addLog(final String log) {
        final String dateStr = FORMAT.format(new Date());
        final SpannableStringBuilder text = new SpannableStringBuilder(dateStr)
                .append(" ")
                .append(log)
                .append("\n");

        text.setSpan(new ForegroundColorSpan(Color.BLUE), 0, dateStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        mLogText.append(text);
    }

    void showLog() {
        mLogText.setText("");
        for (String log : mLogs) {
            addLog(log);
        }
        addLogCleanerRunnable();
    }

    void addLog(final int level, final String tag, final String text) {
        if (TextUtils.isEmpty(tag) || TextUtils.isEmpty(text)) {
            return;
        }

        mLogs.add(tag + ":" + text);

        if (mLogs.size() == 6) {
            mLogs.remove(0);
        }
        showLog();
    }

    void addLogCleanerRunnable() {
        UI_HANDLER.removeCallbacks(mCleanerRunnable);
        mCleanerRunnable = new LogCleanerRunnable();
        UI_HANDLER.postDelayed(mCleanerRunnable, 3000);
    }

    class LogCleanerRunnable implements Runnable {

        @Override
        public void run() {
            if (mLogs.isEmpty()) {
                return;
            }
            mLogs.remove(0);
            showLog();
        }
    }
}
