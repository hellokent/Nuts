# Java端调用Controller方法

## 背景

为了方便其他人员轻松做公司业务的单元测试或者集成测试，需要比较容易的去触发业务逻辑，执行业务操作。
Nuts针对这类需要，利用http+websocket技术将App内的BusinessController、EventBus和Storage与外界打通。可以在PC上调用到手机里面的逻辑。

## 使用过程

1. App初始化过程

Application需要继承NutsApplication，并添加如下代码：

```java
initApiServer()
.registerController(TestController.class, Const.TEST_CONTROLLER)
.registerEventBus(Const.BUS)
.registerListenBus()
.registerStorage(SimpleObject.class, Const.SIMPLE_OBJECT_STORAGE)
.start();
```
2. PC端

PC端使用普通Java项目，需要依赖lib-java。
App实例类为：AppInstance，创建该类需要传入应用包名，和手机对应的IP地址。

Controller调用：

```java
final AppInstance appInstance = new AppInstance("io.demor.nuts.sample", "172.16.141.221");
final TestController controller = Reflection.newProxy(TestController.class, new ControllerInvokeHandler(appInstance));
controller.callListenerInt(1).sync();
```

EventBus调用：
EventBus的调用需要用的到：EventBarrier。EventBarrier创建的时候建立了和App的WebSocket，可以在指定的时间内获取该EventBus发出的所有Event对象。

```java
final AppInstance appInstance = new AppInstance("io.demor.nuts.sample", "172.16.141.221");
final TestController controller = Reflection.newProxy(TestController.class, new ControllerInvokeHandler(mAppInstance));
try (EventBarrier<TestEvent> barrier = new EventBarrier<>(mAppInstance, TestEvent.class);) {
    controller.sendEvent().sync();
    final TestEvent event = barrier.waitForSingleEvent(10, TimeUnit.SECONDS);
}
```
EventBarrier的waitForAllEvent方法，是等待指定的时间，然后返回该时间内的所有Event对象。waitForSingleEvent是等待最多制定时间，假如有指定数据过来，立即返回，否则返回Null。

ListenerBus调用：
ListenerBus的调用需要用的：ListenerBarrier。ListenerBarrier和EventBus类似，也是基于WebSocket。
```java
final AppInstance appInstance = new AppInstance("io.demor.nuts.sample", "172.16.141.221");
final CountDownLatch latch = new CountDownLatch(2);
try (ListenerBarrier barrier = new ListenerBarrier(mAppInstance)) {
    barrier.registerDuring(new SimpleListener() {
        @Override
        public void onGotInt(final int count) {
        }

        @Override
        public void onGotString(final String msg) {
        }
    }, 5, TimeUnit.SECONDS);
}
latch.await(5, TimeUnit.SECONDS);
Assert.assertEquals(0, latch.getCount());
```