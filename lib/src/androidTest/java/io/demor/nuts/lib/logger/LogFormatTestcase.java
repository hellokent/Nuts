package io.demor.nuts.lib.logger;

import junit.framework.TestCase;

public class LogFormatTestcase extends TestCase {

    public void testBase() throws Exception {
        LogFormatter<LogContext> formatter = new LogFormatter<>("%tagaaa",
                LogContext.class);

        LogContext context = new LogContext();
        context.mTag = "TTT";

        assertEquals("TTTaaa", formatter.format(context));
    }

    public void testTwoField() throws Exception {
        LogFormatter<LogContext> formatter = new LogFormatter<>("%tag, %method --- %msg ---aaa",
                LogContext.class);

        LogContext context = new LogContext();
        context.mTag = "TTT";
        context.mMethod = "mmm";
        context.mMsg = "asdf";

        assertEquals("TTT, mmm --- asdf ---aaa", formatter.format(context));
    }

    public void testDuplicateField() throws Exception {
        LogFormatter<LogContext> formatter = new LogFormatter<>("%tag%tagaaa",
                LogContext.class);

        LogContext context = new LogContext();
        context.mTag = "TTT";
        assertEquals("%s%saaa", formatter.mFormat);
        assertEquals("TTTTTTaaa", formatter.format(context));

    }
}
