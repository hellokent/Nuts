package io.demor.nuts.lib;

import io.demor.nuts.lib.controller.AppInstance;
import org.junit.Before;

public class BaseTest {

    protected AppInstance mAppInstance;

    @Before
    public void setUp() throws Exception {
        mAppInstance = new AppInstance("io.demor.nuts.sample", "172.16.141.221");
    }
}
