package io.demor.nuts.lib.logger;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;

public abstract class LogOutput {

    public LogOutput(Element element) throws DOMException {
    }

    protected abstract void append(LogContext context);
}
