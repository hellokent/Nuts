package io.demor.nuts.lib.log;

import android.app.Application;
import android.media.tv.TvContract;
import android.text.TextUtils;
import android.util.Log;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.demor.nuts.lib.log.output.FileOutput;
import io.demor.nuts.lib.log.output.LogcatOutput;
import io.demor.nuts.lib.log.output.WebOutput;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class LoggerFactory {

    private static final HashMap<String, LogOutput> LOG_OUTPUT_MAP = Maps.newHashMap();
    private static final HashMap<String, Logger> LOGGER_OUTPUT_MAP = Maps.newHashMap();
    private static final Set<Logger> LOGGER_SET = Sets.newTreeSet(new Comparator<Logger>() {
        @Override
        public int compare(Logger lhs, Logger rhs) {
            final int r = rhs.mPath.length();
            final int l = lhs.mPath.length();
            return l < r ? 1 : (l == r ? 0 : -1);
        }
    });

    static final Logger DEFAULT_LOG = new Logger("", "") {

        @Override
        protected void log(int level, String msg, Object... args) {
            Log.println(level, "log", args == null || args.length == 0 ? msg : String.format(msg, args));
        }
    };

    private LoggerFactory() {
    }

    public static void readConfigFromAsset(final Application app, final String fileName) {
        InputStream stream = null;
        try {
            stream = app.getAssets().open(fileName);
            LoggerFactory.loadConfig(app, stream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException ignored) {
                }
            }
        }

    }

    //TODO reload config
    public static void reLoadConfig(final Application app, final InputStream stream) {
        clear();
        loadConfig(app, stream);
    }

    public static void loadConfig(final Application app, final InputStream stream) {
        if (app == null) {
            throw new IllegalArgumentException("app cannot be null");
        }
        if (stream == null) {
            throw new IllegalArgumentException("stream cannot be null");
        }
        try {
            final Element root = DocumentBuilderFactory
                    .newInstance()
                    .newDocumentBuilder()
                    .parse(stream)
                    .getDocumentElement();
            NodeList nodeList = root.getElementsByTagName("output");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Element node = (Element) nodeList.item(i);
                final String id = node.getAttribute("id");
                final LogOutput output = parseOutput(node, app);
                if (output != null) {
                    LOG_OUTPUT_MAP.put(id, output);
                }
            }

            nodeList = root.getElementsByTagName("log");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Element node = (Element) nodeList.item(i);
                final String path = node.getAttribute("path");
                final String tag = node.getAttribute("tag");

                if (TextUtils.isEmpty(path) || TextUtils.isEmpty(tag)) {
                    continue;
                }
                final NodeList childNodeList = node.getElementsByTagName("output");
                final Logger logger = new Logger(path, tag);
                LOGGER_SET.add(logger);
                for (int j = 0; j < childNodeList.getLength(); ++j) {
                    final Element childNode = (Element) childNodeList.item(j);
                    final String outputId = childNode.getAttribute("id");
                    if (TextUtils.isEmpty(outputId)) {
                        final LogOutput output = parseOutput(childNode, app);
                        if (output != null) {
                            logger.mLogOutputs.add(output);
                        }
                    } else {
                        final LogOutput output = LOG_OUTPUT_MAP.get(outputId);
                        if (output == null) {
                            continue;
                        }
                        if (logger.mLogOutputs.contains(output)) {
                            continue;
                        }
                        logger.mLogOutputs.add(output);
                    }
                }
                logger.configLoaded();
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

        for (Map.Entry<String, LogOutput> entry : LOG_OUTPUT_MAP.entrySet()) {
            final Logger logger = new Logger("", entry.getKey());
            logger.mLogOutputs.add(entry.getValue());
            LOGGER_OUTPUT_MAP.put(entry.getKey(), logger);
        }
    }

    private static LogOutput parseOutput(final Element node, final Application app) {
        final String type = node.getAttribute("type");
        final String id = node.getAttribute("id");

        if (TextUtils.isEmpty(id) || TextUtils.isEmpty(type)) {
            return null;
        }

        final LogOutput output;
        switch (type) {
            case "logcat":
                output = new LogcatOutput(node);
                break;
            case "file":
                output = new FileOutput(app, node);
                break;
            case "web" :
                output = new WebOutput(node);
                break;
            default:
                try {
                    Object o = Class.forName(type).getConstructor(Element.class).newInstance(node);
                    if (o instanceof LogOutput) {
                        return (LogOutput) o;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
                output = null;
        }
        return output;
    }

    public static Logger getLogger(final Class<?> clz) {
        final String className = clz.getName();
        for (Logger logger : LOGGER_SET) {
            if (className.startsWith(logger.mPath)) {
                return logger;
            }
        }
        return DEFAULT_LOG;
    }

    public static Logger getLogger(final String outputId) {
        if (LOGGER_OUTPUT_MAP.containsKey(outputId)) {
            return LOGGER_OUTPUT_MAP.get(outputId);
        }
        return DEFAULT_LOG;
    }

    public static Logger getLogger() {
        final Thread thread = Thread.currentThread();
        final StackTraceElement element = thread.getStackTrace()[3];
        for (Logger logger : LOGGER_SET) {
            if (element.getClassName().startsWith(logger.mPath)) {
                return logger;
            }
        }
        return DEFAULT_LOG;
    }

    protected static void clear() {
        for (Logger logger : LOGGER_SET) {
            logger.clear();
        }
        LOG_OUTPUT_MAP.clear();
        LOGGER_OUTPUT_MAP.clear();
        LOGGER_SET.clear();
    }
}
