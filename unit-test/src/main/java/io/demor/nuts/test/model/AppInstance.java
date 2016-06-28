package io.demor.nuts.test.model;

import com.google.common.collect.Lists;

import java.util.List;

public class AppInstance {
    public String mName;
    public int mPort;
    public String mPackageName;

    public List<ControllerInfo> mControllers = Lists.newArrayList();
    //TODO eventInfo storageInfo
}
