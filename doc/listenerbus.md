# ListenerBus

## 背景

在使用EventBus的过程中，我们会发现其实EventBus替代着Java里面的接口（Interface）在观察者模式中的地位。通过Eventbus成功得解偶了事件的生成者和消费者。

但是观察者模式全部使用Eventbus会导致如下问题：

* Event类型会剧增
* 在一整套流程中，Event消费者可能会无意间遗漏某一个或几个Event的消费
比如订单流系统这个流程中，业务流会发出创建订单，修改订单，取消订单等若干Event，但是消费方可能只处理的头两个Event，后面的Event忘记处理，这样可能会导致Bug

所以针对这些问题，就需要重新使用Interface，但是也要保证消息的生产和消费解偶。

## 使用方法

首先需要定义Listener接口：

```java
public interface SimpleListener {

    void onGotInt(int count);

    void onGotString(String msg);
}
```
这里需要注意的是，消息发送函数都必须是void返回值类型，因为消息的发送是一个单向的过程，不存在双向的情况。

接下来，利用动态代理创建消息生成者：

```java
SimpleListener SIMPLE_LISTENER = ListenerBus.provide(SimpleListener.class);
```

注册消息接收者：

```java
ListenerBus.register(SimpleListener.class, this);
```

第二个参数需要实现SimpleListener，可以在实现的方法上用注解`Event`来标识调用线程：UI线程、后台线程或者同步调用。

