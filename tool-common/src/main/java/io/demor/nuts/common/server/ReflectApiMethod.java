package io.demor.nuts.common.server;

import com.google.gson.Gson;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class ReflectApiMethod implements IApiMethod {
    public final Gson mGson;
    public Object mImpl;
    public Method mMethod;
    public List<String> mParamList;
    public Class[] mParamTypeList;

    public ReflectApiMethod(Gson mGson) {
        this.mGson = mGson;
    }

    @Override
    public String invoke(final Map<String, String> parameterMap) {
        Object[] param = new Object[mParamTypeList.length];
        for (int i = 0, n = mParamTypeList.length; i < n; ++i) {
            final Class typeClz = mParamTypeList[i];
            final String key = mParamList.get(i);
            final String value = parameterMap.get(key);
            if (typeClz == Integer.class || typeClz == int.class) {
                param[i] = Integer.parseInt(value);
            } else if (typeClz == Long.class || typeClz == long.class) {
                param[i] = Long.parseLong(value);
            } else if (typeClz == String.class) {
                param[i] = value;
            } else {
                throw new RuntimeException(typeClz.getName() + " 还没有实现");
            }
        }
        try {
            Object result = mMethod.invoke(mImpl, param);
            if (result instanceof String) {
                return result.toString();
            } else {
                return mGson.toJson(result);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
