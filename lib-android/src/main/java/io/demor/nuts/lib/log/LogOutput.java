package io.demor.nuts.lib.log;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;

public abstract class LogOutput {

    public LogOutput(Element element) throws DOMException {
    }

    protected abstract void append(LogContext context);

    protected boolean needThreadStack() {
        return false;
    }

    protected boolean needCurrentTime() {
        return false;
    }
}
