# Api模块

## 背景

App开发中的网络层指的是定义并实现和服务器沟通的协议。Nuts的Api模块目前实现了基于Http协议的封装。

网络协议共分为调用个部分：协议的定义、实现和调用。

## 简介

该模块用一个Java接口来描述服务器接口。

<pre><code>
public interface TestApi {
    @Get("test")
    BaseResponse test(@Param("a") String a, @Param("b") String b);

    @Post("url")
    BaseResponse testUrl();
}
</code></pre>

使用的时候，需要传入一个`INet`实现类，在实现类里面，可以定制请求参数。

<pre><code>
TestApi api = Reflection.newProxy(TestApi.class, new ApiInvokeHandler(new INet() {
            @Override
            protected String onCreateUrl(final String url, final Method method, final Object[] args) {
                        return HOST + url;
                }
            }

            @Override
            protected void onCreateParams(final TreeMap<String, String> params, 
                                          final TreeMap<String, String> headers, 
                                          final Method method, final Object[] args) {}
        }, new Gson());
</code></pre>

## 定义协议

该模块使用注解描述请求参数和请求类型。

### Http Url & Method

HttpMethod用不同的Annotation来描述，目前支持的Method有：`@Get`、`@Post`、`@Patch`、`@Put`、`@Delete`，请求的URL写到注解的值里面。

<pre><code>
@Post("user/login")
</code></pre>

这个注解的参数，会传入到`INet`回调的`onCreateUrl`的url参数。

当URL里有动态参数时，需要参数里面传入动态参数，同时在方法的参数里传入带有`@Path`注解的参数值。

<pre><code>
@Get("%s/user")
BaseResponse userInfo(@Path String id);
</code></pre>

设置动态参数，利用的是`String.format()`实现的，参数的顺序会决定url里面%s里面参数的顺序

<pre><code>
@Get("%s/user/comment/%s")
BaseResponse userInfo(@Path String userId,
                      @Path String commentId);
                      
@Get("%s/user/comment/%s")
BaseResponse userInfo(@Path String commentId,
                      @Path String userId);
</code></pre>

上面第一个方法生成的url是{userId}/user/comment/{commentId}，第二个方法生成的是{commentId}/user/comment/{userId}

### Header设置

所有请求需要的统一Header，在`INet`的实现类里面的`onCreateParam`里面添加。

某个请求需要添加特殊的固定Header，可以给方法添加`@Headers`注解

<pre><code>
@Get("header")
@Headers({"r1:h1", "r2:h2"})
BaseResponse header();
</code></pre>

假如需要某个请求需要添加动态Header，使用的方法和动态URL类似，使用的是`@Header`注解。

<pre><code>
@Get("header")
BaseResponse header1(@Header("p1") String header);
</code></pre>

### 请求参数

方法参数设置了Http的请求参数。Http参数需要用`@Param`来设置对应的Key值。

<pre><code>
@Get("test")
BaseResponse test(@Param("a") String a, @Param("b") String b);
</code></pre>

参数支持任何类型。在调用时，会调用`toString()`来生成最终的参数值。

### 上传文件

上传文件使用MultiPart协议，方法使用`@Multipart`注解（其实Multipart使用的也是Post请求，但是这里为了区分，故依然使用Multipart注解）

假如需要监控上传进度，可以传入`ProgressListener`实例，该参数同样需要添加`@Param`注解，并且要求和需要监听的文件使用同一个key值。

<pre><code>
@Multipart("upload")
BaseResponse uploadFile(@Param("file")File file,
                        @Param("file")ProgressListener listener);
</code></pre>

## 实现协议

协议的实现需要两个部分：

1. URL的拼装

2. 通用Header和Parameter的处理

这两个部分分别对应的是`INet`接口里面的`onCreateUrl`和`onCreateParams`这两个方法。

这两个方法里都传入了接口方法的反射Method对象及其参数，这样可以方便其他开发者自定义注解来做二次框架开发。

### 拼装URL

在`onCreateUrl`这个方法里面，传入接口方法中写明的url、接口方法对应的反射Method方法及其参数。返回值是发送请求需要的Url。

底层模块会直接用返回值的结果当初最终的URL来进行网络收发。

### 通用Header和Parameter的处理

在`onCreateParams`这个方法里，传入接口方法中写明的URL、接口方法对应的反射Method方法及其参数、按照接口注解生成的Params和Header的map对象。

此时可以根据业务需要，继续往map里添加需要的通用参数或者接口签名值。

## 返回值处理

该模板的返回值比较特殊，需要实现`IResponse`接口，通过实现该接口来使得返回值对象获取到网络通讯状态。

`IResponse`有三个回调需要实现：

* `setErrorCode(int errorCode)` 

这个回调用来设置返回值的错误码，默认的错误码是0,出错了是-1024（BAD_NETWORK）

* `setStatusCode(int statusCode)`

这个回调用来设置HttpStatusCode。

* `setHeader(Map<String, String> header)`

这个回调用来回调服务器返回Response里面的Header。

## 调用方式

默认的返回值是同步阻塞调用，假如方法的返回值用Return对象包装后，可以同时支持同步和异步调用。