package io.demor.nuts.lib.eventbus;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import io.demor.nuts.lib.annotation.eventbus.DeepClone;

import static io.demor.nuts.lib.ReflectUtils.isSubclassOf;

public final class EventBus{

    private final static Map<Class, ClassContext> CACHE_MAP = Maps.newConcurrentMap();
    private final Multimap<Object, MethodContext> mSlotMap = LinkedListMultimap.create();
    private final List<BaseEvent> mStickEvent = Lists.newCopyOnWriteArrayList();
    private final Executor mBgExecutor, mUiExecutor;
    private IPostListener mPostListener = null;

    public EventBus(Executor bgExecutor, Executor uiExecutor) {
        mBgExecutor = bgExecutor;
        mUiExecutor = uiExecutor;
    }

    public synchronized void register(final Object o) {
        if (o == null) {
            return;
        }
        final Class<?> typeClz = o.getClass();
        ClassContext cc = CACHE_MAP.get(typeClz);
        if (cc == null) {
            cc = new BusClassContext(typeClz);
            CACHE_MAP.put(typeClz, cc);
        }

        if (cc.mMethodList.isEmpty()) {
            return;
        }

        mSlotMap.putAll(o, cc.mMethodList);

        final LinkedList<BaseEvent> deletedEvent = Lists.newLinkedList();
        for (final BaseEvent event : mStickEvent) {
            for (MethodContext methodContext : cc.mMethodList) {
                final BusMethodContext method = (BusMethodContext) methodContext;
                if (isSubclassOf(method.mEventType, event.getClass())) {
                    methodContext.call(o, event);
                    deletedEvent.add(event);
                    break;
                }
            }
        }
        for (BaseEvent event : deletedEvent) {
            mStickEvent.remove(event);
        }
    }

    public synchronized void unregister(final Object o) {
        mSlotMap.removeAll(o);
    }

    public synchronized boolean post(final BaseEvent event) {
        if (event == null) {
            return false;
        }
        if (mPostListener != null) {
            mBgExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    mPostListener.onPostEvent(event);
                }
            });
        }
        final Class<?> clz = event.getClass();
        boolean post = false;
        for (Map.Entry<Object, MethodContext> entry : mSlotMap.entries()) {
            final BusMethodContext method = (BusMethodContext) entry.getValue();
            if (method.mEventType == clz) {
                method.call(entry.getKey(), event);
                post = true;
            }
        }
        return post;
    }

    public synchronized void postStick(final BaseEvent event) {
        if (!post(event)) {
            mStickEvent.add(event);
        }
    }

    public int getStickyEventCount() {
        return mStickEvent.size();
    }

    public void setPostListener(IPostListener postListener) {
        this.mPostListener = postListener;
    }

    private class BusClassContext extends ClassContext {


        public BusClassContext(Class<?> clz) {
            super(clz);
        }

        @Override
        protected MethodContext createMethodContext(Method method, ThreadType threadType) {
            Class<?>[] paramTypes = method.getParameterTypes();
            if (paramTypes == null || paramTypes.length != 1 || !isSubclassOf(paramTypes[0], BaseEvent.class)) {
                return null;
            } else {
                return new BusMethodContext(method, threadType);
            }
        }
    }

    private class BusMethodContext extends MethodContext {

        Class<?> mEventType;
        boolean mNeedDeepClone = false;

        BusMethodContext(Method method, ThreadType threadType) {
            super(method, threadType, mBgExecutor, mUiExecutor);
            mEventType = method.getParameterTypes()[0];
            if (mEventType.getAnnotation(DeepClone.class) != null) {
                mNeedDeepClone = true;
            }
        }

        @Override
        protected boolean needDeepClone() {
            return mNeedDeepClone;
        }
    }
}