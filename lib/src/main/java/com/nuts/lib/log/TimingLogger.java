package com.nuts.lib.log;

import android.os.SystemClock;
import android.util.Log;

import java.util.ArrayList;

import com.google.common.collect.Lists;

public class TimingLogger {

    private String mTag;

    private String mLabel;

    ArrayList<Long> mSplits = Lists.newArrayList();

    ArrayList<String> mSplitLabels = Lists.newArrayList();

    public TimingLogger(String tag, String label) {
        mTag = tag;
        mLabel = label;
        addSplit(null);
    }

    public void addSplit(String splitLabel) {
        long now = SystemClock.elapsedRealtime();
        mSplits.add(now);
        mSplitLabels.add(splitLabel);
    }

    public void dumpToLog() {
        Log.d(mTag, mLabel + ": begin");
        final long first = mSplits.get(0);
        long now = first;
        for (int i = 1; i < mSplits.size(); i++) {
            now = mSplits.get(i);
            final String splitLabel = mSplitLabels.get(i);
            final long prev = mSplits.get(i - 1);

            Log.d(mTag, mLabel + ":      " + (now - prev) + " ms, " + splitLabel);
        }
        Log.d(mTag, mLabel + ": end, " + (now - first) + " ms");
    }
}
