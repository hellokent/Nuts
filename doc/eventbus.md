# EventBus

## 背景

用事务总线的思想解偶事件的发送和接收方的逻辑。

## 使用方法

定义一条EventBus，建议将EventBus定义为全局变量。

<pre><code>
public interface Globals{
       EventBus BUS = new EventBus();
}
</pre></code>

定义一个事件，供EventBus发送和接收用

<pre><code>
public class LoginSuccessEvent extends BaseEvent<Account> {
       public LoginSucessEvent(final Account account) {
       	      super(account);
       }
}
</pre></code>

发送事件

<pre><code>
Globals.BUS.post(new LoginSuccessEvent(account));
</pre></code>

定义事件的接收函数，接收函数所在的类需要提前注册和解注册。
接收函数需要用

```java
public class MainActivity extends Activity {

       @Override()
       public void onCreate() {
              Globals.BUS.register(this); //注册当前Activity
       }

       /**
       * 
       * 添加`@Event`注解后，并且只有一个继承于`BaseEvent`的参数，就会被注册到EventBus里
       * Event注解用来表示该函数需要在哪个线程下执行，有三个选项
       * ThreadType.MAIN:UI线程
       * ThreadType.BACKGROUND:后台线程
       * ThreadType.SOURCE:和事件发送方处于同一线程
       */
       @Event(runOn = ThreadType.MAIN)
       protected void onLoginSuccess(LoginSuccessEvent event) {
       		 //do login success staff...
       }

       @Override()
       public void onDestory() {
              //必须解注册Activity，否则会导致内存泄漏
	      //建议将注册和解注册放到BaseActivity
       	      Globals.BUS.unregister(this);
       }

}
```
</pre></code>

对于正常的EventBus的使用情况，先创建Event，发送Event，然后接收该Event，最后这个Event被GC掉。然而有些时候没有接收函数的时候不希望该Event被马上GC掉，而且希望该Event被保留在内存中，等下次接收函数注册上的时候，由接收函数消费。

<pre><code>
Globals.BUS.postStick(new LoginSuccessEvent(account));
</pre></code>

## ListenerBus

### 背景

随着项目慢慢发展，会发现Event类越来越多，比如一个简单的登录流程，也会发送LoginSuccessEvent、LoginFailedEvent、LoginTimeoutEvent这三个Event，开发调试都很辛苦。
这个问题的简单修改就是提取出一个ILoginStatus的回调函数，但是随着业务发展，这种`setCallback(...)`，`removeCallback()`会写很多遍。
为了一次解决这种问题，Nuts提供了ListenerBus模块。

### 使用过程


<pre><code>
</pre></code>