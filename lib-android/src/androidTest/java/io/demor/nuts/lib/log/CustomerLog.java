package io.demor.nuts.lib.log;

import com.google.common.collect.Lists;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;

import java.util.List;

public class CustomerLog extends LogOutput {

    public static String sArg;
    public static CustomerLog sInstance;
    public static final List<LogContext> LOG_CONTEXT_LIST = Lists.newArrayList();

    public CustomerLog(Element element) throws DOMException {
        super(element);
        sArg = element.getElementsByTagName("arg").item(0).getTextContent();
        sInstance = this;
    }

    @Override
    public void append(LogContext context) {
        LOG_CONTEXT_LIST.add(context);
    }

    @Override
    protected boolean needCurrentTime() {
        return true;
    }

    @Override
    protected boolean needThreadStack() {
        return true;
    }
}
