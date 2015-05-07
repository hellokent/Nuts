package com.nuts.lib.eventbus;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;

import com.google.common.collect.Maps;
import com.nuts.lib.Globals;
import com.nuts.lib.ReflectUtils;
import com.nuts.lib.log.L;
import com.nuts.lib.task.RunnableTask;

public final class EventBus implements Globals {

    final static Map<Class, ClassContext> CACHE_MAP = Maps.newConcurrentMap();

    final Map<Object, ArrayList<MethodContext>> mSlotMap = Maps.newConcurrentMap();

    public final synchronized void register(final Object o) {
        if (o == null) {
            return;
        }
        final Class<?> typeClz = o.getClass();
        ClassContext cc = CACHE_MAP.get(typeClz);
        if (cc == null) {
            cc = new ClassContext(typeClz);
            CACHE_MAP.put(typeClz, cc);
        }

        if (cc.mMethodList.isEmpty()) {
            return;
        }

        mSlotMap.put(o, cc.mMethodList);
    }

    public final synchronized void unregister(final Object o) {
        mSlotMap.remove(o);
    }

    public final synchronized void post(final BaseEvent event) {
        if (event == null) {
            return;
        }
        final Class<?> clz = event.getClass();
        L.v("event:%s from:%s", clz.getSimpleName(), Thread.currentThread()
                .getStackTrace()[3].toString());
        for (Map.Entry<Object, ArrayList<MethodContext>> entry: mSlotMap.entrySet()) {
            for(MethodContext method : entry.getValue()) {
                if (method.mEventType == clz) {
                    method.call(event, entry.getKey());
                }
            }
        }
    }

    static class ClassContext {
        public ArrayList<MethodContext> mMethodList = new ArrayList<MethodContext>();

        public ClassContext(Class<?> clz) {
            while (clz != null) {
                for (Method method : clz.getDeclaredMethods()) {
                    final Event event = method.getAnnotation(Event.class);
                    if (event == null) {
                        continue;
                    }
                    Class<?>[] paramTypes = method.getParameterTypes();
                    if (paramTypes == null || paramTypes.length != 1 || !ReflectUtils.isSubclassOf(paramTypes[0],
                            BaseEvent.class)) {
                        continue;
                    }
                    mMethodList.add(new MethodContext(method));
                }
                clz = clz.getSuperclass();
            }
        }

        @Override
        public String toString() {
            return "ClassContext{" +
                    "mMethodList=" + mMethodList +
                    '}';
        }
    }

    static class MethodContext {
        private final Method mMethod;
        private final Event mEvent;
        private final Class<?> mEventType;

        MethodContext(final Method method) {
            mMethod = method;
            mEvent = method.getAnnotation(Event.class);
            mMethod.setAccessible(true);
            mEventType = method.getParameterTypes()[0];
        }

        public void call(final BaseEvent event, final Object obj) {
            switch (mEvent.runOn()) {
                case SOURCE:
                    $call(event, obj);
                    break;
                case MAIN:
                    UI_HANDLER.post(new Runnable() {
                        @Override
                        public void run() {
                            $call(event, obj);
                        }
                    });
                    break;
                case BACKGROUND:
                    new RunnableTask().safeExecute(new Runnable() {
                        @Override
                        public void run() {
                            $call(event, obj);
                        }
                    });
                    break;
            }
        }

        private void $call(final BaseEvent event, final Object obj) {
            if (obj == null) {
                return;
            }
            try {
                mMethod.invoke(obj, CLONER.deepClone(event));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public String toString() {
            return "MethodContext{" +
                    "mMethod=" + mMethod.getName() +
                    ", mEvent=" + mEvent.runOn().name() +
                    ", mEventType=" + mEventType.getSimpleName() +
                    '}';
        }
    }

}
