package com.nuts.lib.log;

import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.common.io.Files;

/**
 * Created by 陈阳(chenyang@edaijia-staff.cn>)
 * Date: 6/5/14 4:16 PM.
 */
public enum LogWriter {
    INSTANCE;

    final static SimpleDateFormat LOG_TIME = new SimpleDateFormat("MM-dd HH:mm:ss");
    final static SimpleDateFormat LOG_FILE_TIME = new SimpleDateFormat("MM-dd");
    final Handler kWriterHandler = new Handler(new HandlerThread("log-writer") {{start();}}.getLooper());

    final String mDir;
/* Encrypt
    final static int UPLOAD_LOG_DAYS = 3;//上传Log时，上传3天内的Log
    final static String ENCRYPT_KEY = "edaijia15101061387";
    final static SimpleDateFormat LOG_BY_DAY = new SimpleDateFormat("yyyy-MM-dd");
    Cipher cipher;
*/

    private LogWriter() {
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {
            mDir = Environment.getExternalStorageDirectory().getPath();
        } else {
            mDir = null;
        }
/* Encrypt
        try {
            cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.ENCRYPT_MODE, SecretKeyFactory
                            .getInstance("DES")
                            .generateSecret(
                                    new DESKeySpec(ENCRYPT_KEY.getBytes())),
                    new SecureRandom());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
*/
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

/* Encrypt
    public String encrypt(String input) throws Exception {
        return MD5.toHexString(cipher.doFinal(input.getBytes()));
    }

    public boolean uploadLog() {
        final Context context = GlobalApplication.getGlobalContext()
        final String path = Const.ACCOUNT_CONTROLLER.getDriverIdSafely() + "/" + AppInfo.getAppVersion() + "/" + EDJTimeUtil.YMD_2.format(new Date());

        for (int i = 0; i <= UPLOAD_LOG_DAYS; ++i) {
            File logFile = new File(getUploadFileDir(i));
            if (logFile.exists() && !uploadFile(logFile, path + "/log")) {
                return false;
            }
        }
        for (String db : context.databaseList()) {
            if (!uploadFile(context.getDatabasePath(db), path + "/db")) {
                return false;
            }
        }
        File appDir = context.getFilesDir().getParentFile();
        File spDir = new File(appDir, "shared_prefs");
        if (spDir.listFiles() == null) {
            return true;
        }
        for (File spFile : spDir.listFiles()) {
            if (!uploadFile(spFile, path + "/shared_prefs")) {
                return false;
            }
        }
        return true;
    }

    boolean uploadFile(File file, String path) {
        try {
            File tmpFile = new File(file.getParent(), "upload_" + file.getName());
            if ((tmpFile.exists() && !tmpFile.delete()) || !tmpFile.createNewFile()) {
                return false;
            }
            FileUtil.copyFile(file, tmpFile);
            BaseResponse response = ParamBuilder.newBuilder(MethodList.UPLOAD_LOG, BaseResponse.class)
                    .put("file", tmpFile)
                    .put("path", path)
                    .multiPartPost()
                    .sync();
            FileUtil.deleteFile(tmpFile);
            return response.isValid();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    String getUploadFileDir(int lastDays) {
        Calendar calendar = Calendar.getInstance();
        calendar.roll(Calendar.DAY_OF_YEAR, -lastDays);
        return TextUtils.join(File.separator, new String[]{mDir, Const.UPLOAD_LOG_FOLDER, LOG_BY_DAY.format(calendar.getTime())});
    }
*/

}
