package io.demor.nuts.lib.logger;

import io.demor.nuts.lib.log.LogContext;
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

    public void testIllegalField() throws Exception {
        LogFormatter<LogContext> formatter = new LogFormatter<>("bb-%threadId-%aa-%tagaaa", LogContext.class);

        LogContext context = new LogContext();
        context.mTag = "TTT";
        context.mThreadId = 123;

        assertEquals("bb-123-%aa-TTTaaa", formatter.format(context));
    }

    public void testDuplicateField() throws Exception {
        LogFormatter<LogContext> formatter = new LogFormatter<>("%tag%tagaaa",
                LogContext.class);

        LogContext context = new LogContext();
        context.mTag = "TTT";
        assertEquals("TTTTTTaaa", formatter.format(context));
    }
}
