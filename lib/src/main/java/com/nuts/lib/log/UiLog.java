package com.nuts.lib.log;

import android.app.Service;
import android.graphics.PixelFormat;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.nuts.lib.BaseApplication;
import com.nuts.lib.Globals;
import com.nuts.lib.R;

public enum UiLog {
    INSTANCE;

    boolean mShown = false;
    final TextView mSwitch = (TextView) LayoutInflater
            .from(BaseApplication.getGlobalContext())
            .inflate(R.layout.log_switch, null);
    public final LogTagsView mLogTagsView = new LogTagsView();
    final WindowManager mWindowManager = BaseApplication.getService(Service.WINDOW_SERVICE);
    final WindowManager.LayoutParams mSwitchParam = new WindowManager.LayoutParams();
    final WindowManager.LayoutParams mLogTagsParam = new WindowManager.LayoutParams();
    DisplayMetrics mDisplayMetrics = new DisplayMetrics();

    UiLog() {
        mWindowManager.getDefaultDisplay().getMetrics(mDisplayMetrics);
        mSwitch.setOnClickListener(new View.OnClickListener() {
            boolean mShownLogTags = false;

            @Override
            public void onClick(final View v) {
                mShownLogTags = !mShownLogTags;
                if (mShownLogTags) {
                    mWindowManager.addView(mLogTagsView.getRoot(), mLogTagsParam);
                    mSwitch.setText("ON");
                } else {
                    mWindowManager.removeView(mLogTagsView.getRoot());
                    mSwitch.setText("OFF");
                }
            }
        });

        mSwitchParam.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
                | WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
        mSwitchParam.format = PixelFormat.RGBA_8888;
        mSwitchParam.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mSwitchParam.gravity = Gravity.LEFT | Gravity.TOP;
        mSwitchParam.width = dp2px(48);
        mSwitchParam.height = dp2px(48);
        mSwitchParam.x = mDisplayMetrics.widthPixels - mSwitchParam.width;
        mSwitchParam.y = mDisplayMetrics.heightPixels - mSwitchParam.height;


        mLogTagsParam.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
        mLogTagsParam.format = PixelFormat.RGBA_8888;
        mLogTagsParam.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        mLogTagsParam.gravity = Gravity.BOTTOM;
    }

    public void showSwitch() {
        if (mShown) {
            return;
        }
        mWindowManager.addView(mSwitch, mSwitchParam);

        mShown = true;
    }

    public void hideSwitch() {
        if (!mShown) {
            return;
        }

        mWindowManager.removeView(mSwitch);

        mShown = false;
    }

    void onLog(final int level, final String tag, final String text) {
        Globals.UI_HANDLER.post(new Runnable() {
            @Override
            public void run() {
                mLogTagsView.addLog(level, tag, text);
            }
        });
    }

    int dp2px(float dpValue) {
        return (int) (dpValue * mDisplayMetrics.density + 0.5f);
    }

    int sp2px(float sp) {
        return (int) (sp * mDisplayMetrics.scaledDensity + 0.5f);
    }

}
