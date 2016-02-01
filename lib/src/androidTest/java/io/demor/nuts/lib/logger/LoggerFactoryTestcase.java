package io.demor.nuts.lib.logger;

import android.app.Application;
import android.test.AndroidTestCase;

public class LoggerFactoryTestcase extends AndroidTestCase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
        LoggerFactory.readConfigFromAsset((Application) getContext().getApplicationContext(), "logger_testcase.xml");
    }

    public void testLogger() throws Exception {
        assertFalse(LoggerFactory.getLogger() == LoggerFactory.DEFAULT_LOG);
        assertFalse(SelfClass.STATIC_LOGGER == LoggerFactory.DEFAULT_LOG);
        assertEquals("logger", LoggerFactory.getLogger().mTag);
        assertEquals("logger", SelfClass.STATIC_LOGGER.mTag);
        assertTrue(LoggerFactory.getLogger() == LoggerFactory.getLogger());
        assertEquals("static", StaticLog.STATIC_LOGGER.mTag);

        SelfClass.STATIC_LOGGER.v("test:%s", "hello, world!");

    }

    private static class SelfClass {
        private static Logger STATIC_LOGGER = LoggerFactory.getLogger();
    }
}
