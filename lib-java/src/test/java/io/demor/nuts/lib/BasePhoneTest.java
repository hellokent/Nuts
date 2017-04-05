package io.demor.nuts.lib;

import org.junit.Before;

import io.demor.nuts.lib.controller.AppInstance;

public class BasePhoneTest {

    protected AppInstance mAppInstance;

    @Before
    public void setUp() throws Exception {
        mAppInstance = new AppInstance("io.demor.nuts.sample", "172.16.141.221");
    }
}
