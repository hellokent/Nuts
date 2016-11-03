# Nuts --Android 应用中间件

Nuts是用于抽象Android开发中UI逻辑和业务逻辑的中间件，并在此基础上扩展了开发者的能力，使得很方便进行跨App业务逻辑单元测试和UI走查。

## Controller 业务逻辑

以往Android开发中，业务逻辑和UI逻辑往往没有很明显的界限和区分，这样会导致单测困难，代码复用程度低，逻辑Bug较难排查等诸多问题。Nuts的Controller用接口-实现的方式规定了业务逻辑的写法，用动态代理+链式调用改变了方法的调用方式，使得同一个方法可以任意切换同步和异步调用方式。

## Api Network 网络层封装

类Retrofit的Api声明方式，整合了Controller的调用方式，更易用。

## Jumper 页面跳转

接口声明的方式描述界面跳转细节，用动态代理简化了Intent对象的创建过程。

## EventBus/ListenerBus 事件总线

更易用的EventBus。
EventBus对一项业务里面发送的一系列事件不能有很好的支持，很容易漏处理某些Event。
ListenerBus类似于EventBus，但是事件并不是一个Java对象，而是一个接口方法，事件的接收者也必须实现接口进而可以全面处理整个业务系列的所有事件。
Bus都可以自定义传输中间对象的深克隆浅克隆。

## WebDubug UI走查

方便UI走查，用内建WebServer实现了一个简单的UI走查功能，可以显示每一个控件的大小、位置和颜色等属性

## 拓展能力

假如项目里面Controller接口部分，EventBus需要的Event对象、ListenerBus的接口可以暴露成一个纯Java项目，那么就可以在PC上和App打通，Controller通过基于json的RPC协议调用，Bus通过WebSocket实现推送。PC上的调用过程依赖于App的纯Java项目和lib扩展Java项目实现。