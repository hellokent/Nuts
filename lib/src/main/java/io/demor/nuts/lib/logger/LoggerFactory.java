package io.demor.nuts.lib.logger;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import io.demor.nuts.lib.logger.output.FileOutput;
import io.demor.nuts.lib.logger.output.LogcatOutput;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class LoggerFactory {

    protected static final HashMap<String, LogOutput> LOG_OUTPUT_MAP = Maps.newHashMap();
    protected static final HashMap<String, Logger> LOGGER_OUTPUT_MAP = Maps.newHashMap();
    protected static final Set<Logger> LOGGER_SET = Sets.newTreeSet(new Comparator<Logger>() {
        @Override
        public int compare(Logger lhs, Logger rhs) {
            final int r = rhs.mPath.length();
            final int l = lhs.mPath.length();
            return l < r ? 1 : (l == r ? 0 : -1);
        }
    });

    private static final Logger DEFAULT_LOG = new Logger("", "") {

        @Override
        protected void log(int level, String content) {
            Log.println(level, "log", content);
        }
    };

    public static void readConfig(final Application app, final InputStream reader) {
        final Multimap<Logger, String> loggerOutputMap = LinkedHashMultimap.create();
        try {
            final Element root = DocumentBuilderFactory
                    .newInstance()
                    .newDocumentBuilder()
                    .parse(reader)
                    .getDocumentElement();
            NodeList nodeList = root.getElementsByTagName("output");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Element node = (Element) nodeList.item(i);
                final String type = node.getAttribute("type");
                final String id = node.getAttribute("id");

                if (TextUtils.isEmpty(id) || TextUtils.isEmpty(type)) {
                    continue;
                }

                final LogOutput output;
                switch (type) {
                    case "logcat":
                        output = new LogcatOutput(node);
                        break;
                    case "file":
                        output = new FileOutput(app, node);
                        break;
                    default:
                        //TODO LOG EXTENSION
                        continue;
                }
                LOG_OUTPUT_MAP.put(id, output);
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
                for (int j = 0; j < nodeList.getLength(); ++j) {
                    final Element childNode = (Element) childNodeList.item(i);
                    final String outputId = childNode.getAttribute("id");
                    if (TextUtils.isEmpty(outputId)) {
                        continue;
                    }
                    loggerOutputMap.put(logger, outputId);
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

        for (Logger l : loggerOutputMap.keys()) {
            for (String outputId : loggerOutputMap.get(l)) {
                final LogOutput output = LOG_OUTPUT_MAP.get(outputId);
                if (output == null) {
                    continue;
                }
                if (l.mLogOutputs.contains(output)) {
                    continue;
                }
                l.mLogOutputs.add(output);
            }
        }

        for (Map.Entry<String, LogOutput> entry : LOG_OUTPUT_MAP.entrySet()) {
            final Logger logger = new Logger("", entry.getKey());
            logger.mLogOutputs.add(entry.getValue());
            LOGGER_OUTPUT_MAP.put(entry.getKey(), logger);
        }

        LOGGER_SET.addAll(loggerOutputMap.keySet());
    }

    public static Logger getLogger(final Class<?> clz) {
        for (Logger logger : LOGGER_SET) {
            if (logger.mPath.startsWith(clz.getName())) {
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
            if (logger.mPath.startsWith(element.getClassName())) {
                return logger;
            }
        }
        return DEFAULT_LOG;
    }
}
