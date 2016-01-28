package io.demor.nuts.lib.logger.output;

import io.demor.nuts.lib.logger.LogContext;
import io.demor.nuts.lib.logger.LogOutput;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;

public class FileOutput extends LogOutput {

    public FileOutput(Element element) throws DOMException {
        super(element);
    }

    @Override
    public void append(LogContext context) {

    }
}
