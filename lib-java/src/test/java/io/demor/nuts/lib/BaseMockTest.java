package io.demor.nuts.lib;

import org.junit.Before;

import io.demor.nuts.lib.controller.AppInstance;

public class BaseMockTest {

    public static final String APP_ID = "io.demor.nuts.sample";

    protected MockApp mMockApp;
    protected AppInstance mAppInstance;

    @Before
    public void setUp() throws Exception {
        mMockApp = new MockApp(APP_ID);
        mAppInstance = new AppInstance(APP_ID, "127.0.0.1");
    }
}
