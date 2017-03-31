package io.demor.nuts.lib.log;

import android.app.Application;
import android.test.AndroidTestCase;

public class CustomerLogTestcase extends AndroidTestCase {

    Logger logger;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        LoggerFactory.readConfigFromAsset((Application) getContext().getApplicationContext(), "logger_testcase.xml");
        logger = LoggerFactory.getLogger(CustomerLogTestcase.class);
    }

    public void testCustomerLog() throws Exception {
        assertTrue(logger.mLogOutputs.contains(CustomerLog.sInstance));
        assertEquals("value", CustomerLog.sArg);
        logger.i("test:%s", "a");
        Thread.sleep(100);
        assertEquals(1, CustomerLog.LOG_CONTEXT_LIST.size());
        assertEquals("test:a", CustomerLog.LOG_CONTEXT_LIST.get(0).mMsg);
        LogContext context = CustomerLog.LOG_CONTEXT_LIST.get(0);
        assertNotNull(context.mThreadName);
        assertNotNull(context.mTime);
    }
}
