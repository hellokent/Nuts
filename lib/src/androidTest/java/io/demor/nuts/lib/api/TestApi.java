package io.demor.nuts.lib.api;

import io.demor.nuts.lib.annotation.net.*;
import io.demor.nuts.lib.net.ProgressListener;

import java.io.File;

public interface TestApi {

    @Get("test")
    BaseResponse test(@Param("a") String a, @Param("b") String b);

    @Get("url")
    BaseResponse testUrl();

    @Get("empty")
    BaseResponse emptyUrl();

    @Get("header")
    @Headers({"r1:h1", "r2:h2"})
    BaseResponse header();

    @Get("header")
    BaseResponse header1(@Header("p1") String header);

    @Get("%s/user")
    BaseResponse testParamUrl(@Path String id);

    @Multipart("upload")
    BaseResponse uploadFile(@Param("file") File file, @Param("file") ProgressListener listener);
}