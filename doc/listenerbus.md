# ListenerBus

## 背景

在使用EventBus的过程中，我们会发现其实EventBus替代着Java里面的接口（Interface）在观察者模式中的地位。通过Eventbus成功得解偶了事件的生成者和消费者。

但是观察者模式全部使用Eventbus会导致如下问题：

* Event类型会剧增
* 在一整套流程中，Event消费者可能会无意间遗漏某一个或几个Event的消费
比如订单流系统这个流程中，业务流会发出创建订单，修改订单，取消订单等若干Event，但是消费方可能只处理的头两个Event，后面的Event忘记处理，这样可能会导致Bug

所以针对这些问题，就需要重新使用Interface，但是也要保证消息的生产和消费解偶。

## 使用方法

首先需要