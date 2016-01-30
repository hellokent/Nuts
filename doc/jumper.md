# Jumper－－Activity跳转配置化管理

## 背景

Android开发中，需要跳转到Activity的时候，写法通常如下：

<pre><code>
Intent intent = new Intent(getActivity(), TargetActivity.class);
intent.put("arg1", parameter1);
intent.put("arg2", parameter2);
<p>
<p>
getActivity().startActivity(intent);</code></pre>

这种原生的写法有如下问题：

* 无法直观得了解到`TargetActivity`需要的参数名称，参数类型，各个参数是否都是必选项等信息。

* 难以完美一次性将`TargetActity`要替换成`NewTargetActivity`，需要检查所有`startActivity`的调用处。这种工作烦琐并且易出错。

* 创建Intent的过程不容易复用，其他地方还需要跳转到`TargetAcitvity`的时候，上述代码还需要再写一遍。

为了解决这些问题，我们需要一个工具来简化并且配置化管理Activity跳转调用

## 使用方法

Jumper使用接口来定义一个项目或者模块里的Activity跳转：

<pre><code>
public interface Jumper {

    @ActivityInfo(clz = ControllerSimpleActivity.class)
    IntentHandler simpleController();

    @ActivityInfo(clz = ControllerActivity.class)
}
</code></pre>

