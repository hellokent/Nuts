package io.demor.nuts.lib.log;

import android.app.Application;
import android.test.AndroidTestCase;

public class LoggerFactoryTestcase extends AndroidTestCase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
        LoggerFactory.readConfigFromAsset((Application) getContext().getApplicationContext(), "logger_testcase.xml");
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        LoggerFactory.clear();
    }

    public void testLogger() throws Exception {
        assertFalse(LoggerFactory.getLogger() == LoggerFactory.DEFAULT_LOG);
        assertFalse(SelfClass.STATIC_LOGGER == LoggerFactory.DEFAULT_LOG);
        assertEquals("logger", LoggerFactory.getLogger().mTag);
        assertEquals("logger", SelfClass.STATIC_LOGGER.mTag);
        assertTrue(LoggerFactory.getLogger() == LoggerFactory.getLogger());
        assertEquals("static", StaticLog.STATIC_LOGGER.mTag);
        assertTrue(LoggerFactory.getLogger(StaticLog.class) == StaticLog.STATIC_LOGGER);
        SelfClass.STATIC_LOGGER.v("test:%s", "hello, world!");
    }

    public void testGetOutput() throws Exception {
        assertNotNull(LoggerFactory.getLogger("total-file"));
        assertNotNull(LoggerFactory.getLogger("simple-logcat"));
    }

    private static class SelfClass {
        private static Logger STATIC_LOGGER = LoggerFactory.getLogger();
    }
}
