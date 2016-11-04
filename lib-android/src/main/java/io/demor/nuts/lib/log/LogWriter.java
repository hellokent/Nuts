package io.demor.nuts.lib.log;

import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public enum LogWriter {
    INSTANCE;

    final static SimpleDateFormat LOG_TIME = new SimpleDateFormat("MM-dd HH:mm:ss", Locale.getDefault());
    final static SimpleDateFormat LOG_FILE_TIME = new SimpleDateFormat("MM-dd", Locale.getDefault());
    final Handler kWriterHandler = new Handler(new HandlerThread("log-writer") {{start();}}.getLooper());

    final String mDir;

    LogWriter() {
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {
            mDir = Environment.getExternalStorageDirectory().getPath();
        } else {
            mDir = null;
        }
    }

    public void postWriteRequest(final String path, final String log) {
        if (TextUtils.isEmpty(mDir)) {
            return;
        }
        kWriterHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    final File logFile = new File(mDir + File.separator + path + "_" + LOG_FILE_TIME.format(new Date()) + ".txt");
                    if (!logFile.getParentFile().exists()) {
                        logFile.getParentFile().mkdirs();
                    }
                    Files.append(String.format("%s : %s\r\n", LOG_TIME.format(new Date()), log), logFile, Charset
                            .defaultCharset());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void postUploadLog(final String log) {
    }
}
