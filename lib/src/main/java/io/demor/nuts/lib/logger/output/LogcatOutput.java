package io.demor.nuts.lib.logger.output;

import android.util.Log;
import io.demor.nuts.lib.logger.LogContext;
import io.demor.nuts.lib.logger.LogFormatter;
import io.demor.nuts.lib.logger.LogOutput;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class LogcatOutput extends LogOutput {

    protected final LogFormatter<LogContext> mFormatter;
    private boolean mNeedTime, mNeedThreadStack;

    public LogcatOutput(Element element) throws DOMException {
        super(element);
        final NodeList formatList = element.getElementsByTagName("format");
        if (formatList.getLength() == 0) {
            mFormatter = new LogFormatter<>("%msg", LogContext.class);
        } else {
            Element formatElement = (Element) formatList.item(0);
            mFormatter = new LogFormatter<>(formatElement.getTextContent(), LogContext.class);
        }
        mNeedTime = mFormatter.containsKey("time");
        mNeedThreadStack = mFormatter.containsKey("class") || mFormatter.containsKey("method");
    }

    @Override
    public void append(LogContext context) {
        log(context.mLevel, context.mTag, mFormatter.format(context));
    }

    protected void log(final int level, final String tag, final String text) {
        Log.println(level, tag, text);
    }

    @Override
    protected boolean needCurrentTime() {
        return mNeedTime;
    }

    @Override
    protected boolean needThreadStack() {
        return mNeedThreadStack;
    }
}
