package io.demor.nuts.lib.logger.output;

import android.content.Context;
import android.os.Environment;
import io.demor.nuts.lib.annotation.log.LogFormatKeyword;
import io.demor.nuts.lib.log.LogDay;

public class LogFileContext {

    @LogFormatKeyword("day")
    public LogDay mDay = new LogDay();

    @LogFormatKeyword("context")
    public String mContext;

    @LogFormatKeyword("sdcard")
    public String mSdPath;

    @LogFormatKeyword("app")
    public String mPackageName;

    @LogFormatKeyword("tag")
    public String mTag;

    public LogFileContext(final Context context) {
        mContext = context.getFilesDir().getPath();
        mSdPath = Environment.getExternalStorageDirectory().getPath();
        mPackageName = context.getPackageName();
    }
}
