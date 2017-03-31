# Android中间件

## 背景

在实际的Android开发过程中，会遇到比较复杂的业务逻辑，这部分业务逻辑对于整个App至关重要，举个例子，比如O2O项目的订单流的处理，聊天社交App的聊天消息处理逻辑（比如收发消息，去重，丢失消息的再次拉取等）。

这种类型的逻辑，有这么几个特征：

* 一个业务逻辑动作，很可能同时包含网络收发，DB读写，SharedPreference读写等耗时操作
* 日后更新频繁，需要很好的可维护性和代码可读性
* 调用处较多，而且可能是UI部分调用（Activity，Fragment），也可能是其他业务处理过程中调用（比如在O2O应用中登录完成之后，调用优惠券逻辑，获取全面优惠券）
* 有些耗时调用在前台UI调用时，需要一个生命周期回调，来做调用前后Dialog的显示和消失

## Nuts实现的特性

Nuts认为，处理耗时任务最简单方便的方法，是同步调用。无论是网络、DB或者是线程操作，同步操作是最简单可靠的。

复杂的需求里面，业务之间肯定会有相互调用的情况。业务逻辑里面的耗时请求都是同步调用的话，那么也应该同样用同步的方式调用其他业务的逻辑。

UI需要异步调用业务逻辑（否则会ANR），然而业务之间的调用需要同步访问，这样就要求一个业务逻辑需要同时支持同步异步调用。

重要的话说三遍：

* 业务逻辑需要同时支持同步异步调用。

* 业务逻辑需要同时支持同步异步调用。

* 业务逻辑需要同时支持同步异步调用。

这个就是Nuts中间件（暂称为Controller）的核心特性。

## 解决方案

### 定义业务

Nuts认为，一个业务是有一系列业务逻辑组成。业务逻辑就是业务接口里面的方法。外界通过业务里面的接口方法访问具体的业务逻辑。

业务的实现，需要写一个业务接口的实现类。

用动态代理生成一个业务实例。

每回创建业务实例会略耗时，同时为了方便业务间互相调用，所以需要将业务实例成为静态变量。

```java
public interface Const {
    AccountController ACCOUNT_CONTROLLER = new ProxyInvokeHandler<AccountController>(new AccountControllerImpl()).createProxy();
}
```

### 定义业务逻辑

业务逻辑，是指业务接口的函数声明及其具体实现。

在接口里声明函数的时候，对于不需要同步异步调用的方法，就直接声明实现即可。

需要支持同步异步调用的函数，需要用Return对象封装一下函数返回值，对于void类型的返回值，使用VoidReturn对象。

```java
public interface AccountController {

    String token(); //获取当前用户token

    Return<Boolean> login(String account, String pwd); //登录操作，UI调用需要异步处理
    
    VoidReturn logout(); //登出操作
}
```

### 实现业务逻辑

返回值需要被Return对象包装一次再返回出来，示例如下：

```java
public class AccountControllerImpl implements AccountController {

     @Override
     public Return<Boolean> login(final String account, final String pwd) {
          // add some code to do login task
          return new Return<>(true);
     }
     
     @Override
     public VoidReturn logout() {
         // add some code to do logout task
         return new VoidReturn();
     }
}
```

假如是没有被Return包装的，直接返回即可，示例如下：

```java
@Override
public String token() {
    return hasLogin() ? USER_INFO.get().mToken : "";
}
</code></pre>

### 调用方式

调用业务实例里面的调用方法，返回的其实是Return对象，并不是调用方需要的返回值，需要知道同步异步方法之后，才能获取到真正的返回值。

同步调用，就是阻塞当前线程，直到方法在线程池中执行结束，最后将执行结果传至当前线程。

异步调用，就是不阻塞UI线程，方法执行接收后，将方法的执行结果通知给预先注册好的Callback。

接下来分别介绍同步调用和异步调用的具体方式：

#### 1. 异步调用

业务逻辑的方法一经调用，就马上会在线程池中执行，这个是最简单的异步调用。

异步调用通过Return对象async开头的函数族来实现。

很多时候，调用方都会关心方法执行的结果：

```java
ACCOUNT_CONTROLLER.login(account, password)
        .asyncUIWithDialog(new ControllerCallback<Boolean>() {
            @Override
            public void onResult(final Boolean result) {
            }
        },);
```

#### 2. 同步调用

同步调用很简单，sync方法：

```java
ACCOUNT_CONTROLLER.logout().sync();
```

一般sync方法出现业务之间调用会使用到。

### Controller生命周期

在异步调用的时候，可以添加一个回调来监听这个方法调用前，调用后，调用时抛异常这三种情况：

```java
public interface TestController {
    Return<String> run(int count);
}

TEST_CONTROLLER.run(1)
        .addListener(new ControllerListener<String>() {
            @Override
            public void onPrepare() {}

            @Override
            public void onInvoke(final String response) {}

            @Override
            public void onThrow(final Throwable throwable) {}
        })
        .asyncUI(new ControllerCallback<String>() {
            @Override
            public void onResult(final String s) {
                ToastUtil.showMessage("onResult: " + s);
            }
        });
```

addListener是链式调用，可以添加多个Listener。

### Activity生命周期检查

在Activity、Fragment或者自定义View里面调用业务逻辑的时候，假如需要在当前Activity被Destroy之前，结束当前所有调用，可以使用checkActivity属性。

checkActivity，就是设置在回调和业务逻辑方法执行前，检查Activity是否存活，假如没有存活，则直接返回，没有下一步的执行。

设置checkActivity有两种方式：

1. 业务方法上添加注解

```java
public interface TestController {

    @CheckActivity
    VoidReturn runCheckActivity();
}
```

2. Return对象的链式调用里设置checkActivity

```java
TEST_CONTROLLER.run(1)
        .setNeedCheckActivity(true) //这里设置checkActivity
        .asyncUI(new ControllerCallback<String>() {
            @Override
            public void onResult(final String s) {
                ToastUtil.showMessage("onResult");
            }
        });
```


### 异常处理

每一个业务逻辑，有的时候需要抛出特定异常，提示给调用处，由调用处来处理。

抛异常的时候，需要用ExceptionWrapper包装过后才能抛出，在接口和实现的方法声明处不需要用throws声明异常。

当是异步调用时，需要实现传入的ControllerListener里面有`onThrow`的方法：

```java
public void onThrow(Exception e) {}
```

//TODO 异步处理回调，同步异常处理，异常包装 

### 超时设置

## 最佳实践

1. 业务里面的方法，需要跟着需求来，而不是跟着服务器接口来定义

2. 对于特别复杂的业务逻辑方法，可以试着拆分，定义一些简单的方法，然后慢慢组合出复杂的方法。

3. 一个业务接口可以对应多个业务实现，比如定位业务，可以有百度和高德的不同实现。这样假如产品要求更新地图，只要替换不同实现类就可以。
