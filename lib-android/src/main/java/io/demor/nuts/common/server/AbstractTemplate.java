package io.demor.nuts.common.server;

import com.google.common.collect.Maps;

import java.util.HashMap;
import java.util.Map;

public class AbstractTemplate implements ITemplate {

    protected final HashMap<String, Object> mParam = Maps.newHashMap();

    @Override
    public Map<String, Object> getParam(Map<String, String> param) {
        return mParam;
    }
}
