package com.nuts.test.api;

import java.io.File;

import com.nuts.lib.annotation.net.Get;
import com.nuts.lib.annotation.net.Header;
import com.nuts.lib.annotation.net.Headers;
import com.nuts.lib.annotation.net.Multipart;
import com.nuts.lib.annotation.net.Param;
import com.nuts.lib.annotation.net.Path;
import com.nuts.lib.net.ProgressListener;

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