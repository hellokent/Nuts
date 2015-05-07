package com.nuts.lib.jumper;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.text.TextUtils;

import java.io.File;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import com.google.common.collect.Lists;
import com.nuts.lib.BaseApplication;
import com.nuts.lib.BuildConfig;
import com.nuts.lib.ReflectUtils;
import static com.nuts.lib.ReflectUtils.checkGenericType;
import static com.nuts.lib.ReflectUtils.isSubclassOf;

/**
 * ActivityJumper模块的反射代理实现
 * Created by chenyang.coder@gmail.com on 14-3-2 下午3:03.
 */
public class JumperInvokeHandler implements InvocationHandler {

    public final static String INTENT_DEBUG_STACK = "debug_stack";

    public static final HashMap<Class, Method> INTENT_PUT_EXTRA_MAP = new HashMap<Class, Method>() {

        final Map<Class, Class> mCastMap = Collections.unmodifiableMap(
                new HashMap<Class, Class>() {
                    {
                        put(Byte.class, byte.class);
                        put(Byte[].class, byte[].class);

                        put(Boolean.class, boolean.class);
                        put(Boolean[].class, boolean[].class);

                        put(Character.class, char.class);
                        put(Character[].class, char[].class);

                        put(Short.class, short.class);
                        put(Short[].class, short[].class);

                        put(Integer.class, int.class);
                        put(Integer[].class, int[].class);

                        put(Long.class, long.class);
                        put(Long[].class, long[].class);

                        put(Float.class, float.class);
                        put(Float[].class, float[].class);

                        put(Double.class, double.class);
                        put(Double[].class, double[].class);
                    }
                }
        );

        @Override
        public Method get(final Object key) {
            Method result = super.get(key);
            return result == null ? super.get(mCastMap.get(key)) : result;
        }
    };

    static {
        Method[] methods = Intent.class.getMethods();
        for (Method m : methods) {
            if ("putExtra".equals(m.getName())) {
                INTENT_PUT_EXTRA_MAP.put(m.getParameterTypes()[1], m);
            }
        }
    }

    final static Intent INVALID_INTENT = new Intent();

    final HashMap<Method, Intent> INTENT_CACHE = new HashMap<Method, Intent>() {

        Intent invalidIntent(Method method) {
            put(method, INVALID_INTENT);
            return INVALID_INTENT;
        }

        @Override
        public Intent get(final Object key) {
            Intent result = super.get(key);
            if (result != null) {
                return new Intent(result);
            }
            final Method method = (Method) key;
            assert method != null;

            final ActivityInfo aInfo = method.getAnnotation(ActivityInfo.class);
            final BroadcastInfo bInfo = method.getAnnotation(BroadcastInfo.class);
            if (aInfo == null && bInfo == null) {
                return invalidIntent(method);
            }

            final Intent intent = new Intent();
            if (bInfo != null) {
                intent.setAction(bInfo.value());
                put(method, intent);
            } else {
                if (aInfo.clz() != Activity.class) {
                    intent.setClass(mContext, aInfo.clz());
                }

                if (!TextUtils.isEmpty(aInfo.action())) {
                    intent.setAction(aInfo.action());
                }

                for (int flag : aInfo.defaultFlags()) {
                    intent.addFlags(flag);
                }
                put(method, intent);
            }
            return super.get(method);

        }
    };

    final Context mContext;

    public JumperInvokeHandler(Application application) {
        mContext = application;
    }

    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {

        final Intent intent = INTENT_CACHE.get(method);
        if (intent == null || intent == INVALID_INTENT) {
            return null;
        }
        if (BuildConfig.DEBUG) {
            intent.putExtra(INTENT_DEBUG_STACK, Thread.currentThread().getStackTrace()[4].toString());
        }

        final boolean needReturnIntent = method.getReturnType() == Intent.class;
        final BroadcastInfo broadcastInfo = method.getAnnotation(BroadcastInfo.class);
        final boolean sendBroadcast = broadcastInfo != null;

        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        LinkedList<ParamHolder> holderList = Lists.newLinkedList();

        final IntentType intentType = method.getAnnotation(IntentType.class);
        final String type = intentType == null ? "" : intentType.value();

        Type[] genericTypes = method.getGenericParameterTypes();
        for (int i = 0, n = parameterAnnotations.length; i < n; ++i) {
            if (parameterAnnotations[i] == null) {
                continue;
            }
            final Annotation annotation = parameterAnnotations[i][0];
            if (annotation == null) {
                continue;
            }
            final Object arg = args[i];
            Class<?> argClz = arg == null ? null : arg.getClass();

            if (arg == null) {
                holderList.add(new ParamHolder(parameterAnnotations[i], i, genericTypes[i]));
            } else if (annotation instanceof IntentFlag &&
                    (argClz == Integer.class || argClz == int.class)) {
                intent.addFlags((Integer) arg);
            } else if (annotation instanceof IntentUri) {
                final Uri data;
                if (argClz == Uri.class) {
                    data = (Uri) arg;
                } else if (argClz == File.class) {
                    data = Uri.fromFile((File) arg);
                    intent.setData(Uri.fromFile((File) arg));
                } else if (argClz == String.class) {
                    data = Uri.parse((String) arg);
                } else {
                    data = null;
                }
                intent.setDataAndType(data, type);
            } else if (annotation instanceof IntentType && argClz == String.class) {
                intent.setType((String) arg);
            } else if (annotation instanceof Extra) {
                holderList.add(new ParamHolder(parameterAnnotations[i], i, genericTypes[i]));
            }
        }

        for (ParamHolder holder : holderList) {
            Object o = args[holder.index];
            if (o == null) {
                if (holder.option) {
                    continue;
                } else {
                    if (BuildConfig.DEBUG) {
                        throw new InvalidParameterException("Jumper:" + method.getName());
                    } else {
                        new Exception("Jumper:" + method.getName()).printStackTrace();
                    }
                    return needReturnIntent ? intent : null;
                }
            }
            final Class<?> clz = o.getClass();
            final String key = holder.key;

            final Method m = INTENT_PUT_EXTRA_MAP.get(clz);
            if (m != null) {
                m.invoke(intent, key, o);
            } else if (isSubclassOf(clz, Serializable.class)) {
                intent.putExtra(key, (Serializable) o);
            } else if (isSubclassOf(clz, Parcelable.class)) {
                intent.putExtra(key, (Parcelable) o);
            } else if (isSubclassOf(clz, Parcelable[].class)) {
                intent.putExtra(key, (Parcelable[]) o);
            } else if (isSubclassOf(clz, ArrayList.class)) {
                final Type listType = holder.genericType;
                ArrayList list = (ArrayList) o;
                if (checkGenericType(listType, String.class)) {
                    intent.putStringArrayListExtra(key, list);
                } else if (checkGenericType(listType, Integer.class)) {
                    intent.putIntegerArrayListExtra(key, list);
                } else if (checkGenericType(listType, CharSequence.class)) {
                    intent.putCharSequenceArrayListExtra(key, list);
                } else if (checkGenericType(listType, o.getClass())) {
                    intent.putParcelableArrayListExtra(key, list);
                }
            }
        }

        if (sendBroadcast) {
            if (broadcastInfo.local()) {
                BaseApplication.getGlobalContext().sendBroadcast(intent);
            } else {
                mContext.sendBroadcast(intent);
            }
            return null;
        } else if (needReturnIntent) {
            return intent;
        } else if (isSubclassOf(method.getReturnType(), IntentHandler.class)) {
            return ReflectUtils.newInstance(method.getReturnType(), intent, method);
        } else {
            return null;
        }
    }

    static class ParamHolder {
        String key;
        int index;
        boolean option = false;
        Type genericType;

        ParamHolder(Annotation[] annotations, int index, Type type) {
            for (Annotation a : annotations) {
                if (a instanceof Extra) {
                    Extra extra = (Extra) a;
                    this.key = extra.value();
                    this.option = extra.option();
                    break;
                }
            }
            this.index = index;
            this.genericType = type;
        }
    }
}
