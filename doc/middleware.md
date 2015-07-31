# Android中间件

## 背景

在实际的Android开发过程中，会遇到比较复杂的业务逻辑，这部分业务逻辑对于整个App至关重要，举个例子，比如O2O项目的订单流的处理，聊天社交App的聊天消息处理逻辑（比如收发消息，去重，丢失消息的再次拉取等）。

这种类型的逻辑，有这么几个特征：

* 一个业务逻辑动作，很可能同时包含网络收发，DB读写，SharedPreference读写等耗时操作
* 日后更新频繁，需要很好的可维护性和代码可读性
* 调用处较多，而且可能是UI部分调用（Activity，Fragment），也可能是其他业务处理过程中调用（比如在O2O应用中登录完成之后，调用优惠券逻辑，获取全面优惠券）
* 有些耗时调用在前台UI调用时，需要一个生命周期回调，来做调用前后Dialog的显示和消失

## Nuts实现的特性

Nuts认为，处理耗时任务最简单方便的方法，就是全部是同步调用。一个业务逻辑的实现里面，无论是网络、DB或者是线程操作，同步操作简单可靠。

随着业务规模的发展，业务之间肯定会有相互调用的情况，比如业务A需要调用业务B的逻辑。假如业务逻辑里面网络、DB请求都是同步调用，那么用同步的方式调用其他业务的逻辑，也是顺利成章的事情。

假如UI调用业务逻辑需要异步访问，业务之间的调用需要同步访问，这样就要求一个业务逻辑需要同时支持同步异步调用。

重要的话说三遍：

* 业务逻辑需要同时支持同步异步调用。

* 业务逻辑需要同时支持同步异步调用。

* 业务逻辑需要同时支持同步异步调用。

这个就是Nuts中间件（暂称为Controller）的核心特性。

## 解决方案

### 定义业务

Nuts认为，一个业务是有一系列业务逻辑组成，业务对外暴露一个接口。外界通过这个接口访问具体的业务逻辑。

业务的实现，需要写一个实现类。业务实例对外用动态代理生成一个实例。

每回创建实例会略耗时，而且考虑到业务间互相调用，所以需要将业务实例成为静态变量。

举例：
<pre><code>
public interface Const {
    AccountController ACCOUNT_CONTROLLER = new ProxyInvokeHandler<AccountController>(new AccountControllerImpl()).createProxy();
}
</code></pre>

### 定义业务逻辑

业务逻辑，简单得说就是Nus接口下的一个函数声明和具体实现。

在接口里声明函数的时候，对于不需要同步异步调用的方法，就直接声明实现即可。

需要支持同步异步调用的函数，需要用Return对象封装一下函数返回值，对于void类型的返回值，使用VoidReturn对象。

举例：

<pre><code>
public interface AccountController {

    String token(); //获取当前用户token

    Return<Boolean> login(String account, String pwd); //登录操作，UI调用需要异步处理
    
    VoidReturn logout(); //登出操作
}
</code></pre>

### 实现业务逻辑

在业务逻辑的实现中，假如返回值类型被Return包装过的，那么返回的对象也需要包装一次。

<pre><code>
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
</code></pre>

假如是不需要Return保证的，直接返回即可，示例如下：

<pre><code>
@Override
public String token() {
    return hasLogin() ? USER_INFO.get().mToken : "";
}
</code></pre>

### 调用方式

通过刚刚生成的实例调用方法，返回的其实是Return对象。调用之后，该方法会马上在一个线程池中执行。

同步调用，就是阻塞当前线程，直到方法在线程池中执行结束，然后将执行结果传至当前线程，然后恢复执行。

异步调用，就是不阻塞UI线程，方法执行接收后，将方法的执行结果通知给预先注册好的Callback。

接下来分别介绍同步调用和异步调用的具体方式：

#### 1. 异步调用

业务逻辑的方法一经调用，就马上会在线程池中执行，这个是最简单的异步调用。

异步调用，就是Return对象async开头的函数族。

很多时候，调用方都会关心方法执行的结果：

<pre><code>
ACCOUNT_CONTROLLER.logout().asyncUI(new ControllerCallback<Void>() {
    @Override
    public void onResult(final Void aVoid) {
        // 回调方法执行处，这里的代码会在UI线程中执行
    }
});
</code></pre>

在很多时候，调用前后会显示一个Dialog提示用户：

<pre><code>
ACCOUNT_CONTROLLER.logout().asyncUIWithDialog(new ControllerCallback<Void>() {
    @Override
    public void onResult(final Void aVoid) {
        // 回调方法执行处，这里的代码会在UI线程中执行
    }
}, Dialogs.createLoadingDialog(this));
</code></pre>

asyncUIWithDialog这个方法需要传入一个Dialog参数，用来通知Nuts在方法调用前后需要展示和消失哪个Dialog。

#### 2. 同步调用

同步调用最简单，sync方法：

<pre><code>
ACCOUNT_CONTROLLER.logout().sync();
</code></pre>

一般sync方法出现业务之间调用会使用到。

### Controller生命周期

在异步调用的时候，可以添加一个回调来监听这个方法调用前，调用后，调用时抛异常这三种情况：

<pre><code>
public interface TestController {
    Return<String> run(int count);
}

TEST_CONTROLLER.run(1)
        .addListener(new ControllerListener<String>() {
            @Override
            public void onBegin() {
                L.v("onBegin");
            }

            @Override
            public void onEnd(final String response) {
                L.v("onEnd:%s", response);
            }

            @Override
            public void onException(final Throwable throwable) {
                ToastUtil.showMessage("onException");
            }
        })
        .asyncUI(new ControllerCallback<String>() {
            @Override
            public void onResult(final String s) {
                ToastUtil.showMessage("onResult: " + s);
            }
        });
</code></pre>

addListener是链式调用，可以添加多个Listener。

### Activity生命周期检查

在Activity、Fragment或者自定义View里面调用业务逻辑的时候，假如需要在当前Activity被Destroy之前，结束当前所有调用，可以使用checkActivity属性。

checkActivity，就是设置在回调和业务逻辑方法执行前，检查Activity是否存活，假如没有存活，则直接返回，没有下一步的执行。

设置checkActivity有两种方式：

1. 业务方法上添加注解

<pre><code>
public interface TestController {

    @CheckActivity
    VoidReturn runCheckActivity();
}
</code></pre>

2. Return对象的链式调用里设置checkActivity

<pre><code>
TEST_CONTROLLER.run(1)
        .setNeedCheckActivity(true) //这里设置checkActivity
        .asyncUI(new ControllerCallback<String>() {
            @Override
            public void onResult(final String s) {
                ToastUtil.showMessage("onResult");
            }
        });
</code></pre>


### 异常处理

每一个业务逻辑，有的时候需要抛出特定异常，提示给调用处，由调用处来处理。

抛异常的时候，需要用ExceptionWrapper包装过后才能抛出，在接口和实现的方法声明处不需要用throws声明异常。

当是异步调用时，需要实现传入的ControllerListener里面有handleException的方法：

<pre><code>
public void handleException(Exception e) {}
</code></pre>



