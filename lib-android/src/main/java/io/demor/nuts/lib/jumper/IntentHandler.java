package io.demor.nuts.lib.jumper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import io.demor.nuts.lib.NutsApplication;

import java.lang.reflect.Method;

public class IntentHandler {
    final Intent mIntent;
    final Method mMethod;

    public IntentHandler(final Intent intent, final Method method) {
        mIntent = intent;
        mMethod = method;
    }

    protected Method getMethod() {
        return mMethod;
    }

    public void startActivity(final Activity activity) {
        activity.startActivity(mIntent);
    }

    public void startActivity(final Context context) {
        context.startActivity(mIntent);
    }

    public Intent getIntent() {
        return mIntent;
    }

    public void startActivityForResult(final Activity activity, int requestCode) {
        activity.startActivityForResult(mIntent, requestCode);
    }

    public IntentHandler addFlag(final int flag) {
        mIntent.addFlags(flag);
        return this;
    }

    public IntentHandler addCategory(final String catalog) {
        mIntent.addCategory(catalog);
        return this;
    }

    public IntentHandler setAction(final String action) {
        mIntent.setAction(action);
        return this;
    }

    public void sendBroadcast(final Context context) {
        context.sendBroadcast(mIntent);
    }

    public void sendBroadcast() {
        NutsApplication.getGlobalContext()
                .sendBroadcast(mIntent);
    }

}
