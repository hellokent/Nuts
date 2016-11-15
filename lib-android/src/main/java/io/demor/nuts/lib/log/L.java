package io.demor.nuts.lib.log;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import io.demor.nuts.lib.NutsApplication;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public final class L {
    private static final String DEFAULT_TAG = "log";

    private static final Context CONTEXT = NutsApplication.getGlobalContext();
    private static final int STACK_DEPTH = 4;

    private static final List<LogConfigItem> CONFIG_ITEMS = Lists.newArrayList();

    private static boolean sInited = false;

    static {
        init();
    }

    private L() {
    }

    private static void init() {
        InputStream is = null;
        try {
            if (CONTEXT == null || CONTEXT.getAssets() == null) {
                return;
            }
            is = CONTEXT.getAssets().open("log.xml");
            final XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
            parser.setInput(is, "UTF-8");

            int eventType = parser.getEventType();
            LogConfigItem item;
            String defaultPackage = "";
            while (eventType != XmlPullParser.END_DOCUMENT) {

                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        switch (parser.getDepth()) {
                            case 1:
                                if (parser.getAttributeCount() > 0) {
                                    defaultPackage = parser.getAttributeName(0).equalsIgnoreCase("package") ?
                                            parser.getAttributeValue(0) : "";
                                }
                                break;
                            case 2:
                                item = new LogConfigItem(parser, defaultPackage);
                                CONFIG_ITEMS.add(item);
                                break;
                        }
                        break;
                }
                eventType = parser.next();
            }
            sInited = true;
        } catch (IOException | XmlPullParserException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    /**
     * 这是一个代码异味(code smell)。它可以用于开发过程中追踪bug，但不要提交到你的版本控制系统
     */
    public static void v(final String msg, final Object... args) {
        log(Log.VERBOSE, null, msg, args);
    }

    /**
     * 把一切东西都记录在这里。这在debug过程中最常用到。主张在进入生产阶段前减少debug语句的数量，
     * 只留下最有意义的部分，在调试(troubleshooting)的时候激活。
     */
    public static void d(final String msg, final Object... args) {
        log(Log.DEBUG, null, msg, args);
    }

    /**
     * 用户行为(user-driven)和系统的特定行为(例如计划任务…)
     */
    public static void i(final String msg, final Object... args) {
        log(Log.INFO, null, msg, args);
    }

    /**
     * 录在这个级别的事件都有可能成为一个error。
     * 例如，一次调用数据库使用的时间超过了预设时间，或者内存缓存即将到达容量上限。
     * 这可以让你适当地发出警报，或者在调试时更好地理解系统在failure之前做了些什么
     */
    public static void w(final String msg, final Object... args) {
        log(Log.WARN, null, msg, args);
    }

    /**
     * 把每一个错误条件都记录在这。例如API调用返回了错误，或是内部错误条件
     */
    public static void e(final String msg, final Object... args) {
        log(Log.ERROR, null, msg, args);
    }

    public static void exception(final Throwable throwable) {
        log(Log.WARN, throwable, "");
    }

    private static void log(int level, Throwable throwable, String msg, Object... arg) {
        final String className = Thread.currentThread().getStackTrace()[STACK_DEPTH].getClassName();
        log(className, level, throwable, msg, arg);
    }

    protected static void log(final String className, int level, Throwable throwable, String msg, Object... arg) {
        final String logText = throwable == null ? String.format(msg, arg) :
                Throwables.getStackTraceAsString(throwable) + "\n" + String.format(msg, arg);
        if (!sInited) {
            init();

            if (!sInited) {
                l(level, DEFAULT_TAG, logText);
                return;
            }

        }

        // 检查className
        LogConfigItem logConfig = null;
        for (LogConfigItem item : CONFIG_ITEMS) {
            if (item.checkClassName(className)) {
                if (logConfig == null) {
                    logConfig = item;
                } else if (item.getPackage().length() > logConfig.getPackage().length()) {
                    logConfig = item;
                }
            }
        }

        if (logConfig != null && logConfig.isUpload()) {
            LogWriter.INSTANCE.postUploadLog((logConfig.getTag()) + "\t" + logText);
        }

        if (logConfig == null) {
            l(level, DEFAULT_TAG, logText);
            return;
        }

        // 过滤level & enable
        if (!logConfig.getLevel().isValidLevel(level) || !logConfig.isEnable()) {
            //l(level, DEFAULT_TAG, logText);
            return;
        }

        l(level, logConfig.getTag(), logText);
        if (!TextUtils.isEmpty(logConfig.getFile())) {
            LogWriter.INSTANCE.postWriteRequest(logConfig.getFile(), logText);
        }
    }


    static void l(int level, String tag, String text) {
        Log.println(level, tag, text);
    }
}