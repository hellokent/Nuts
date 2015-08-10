package com.nuts.test.api;

import com.nuts.lib.annotation.net.Get;
import com.nuts.lib.annotation.net.Header;
import com.nuts.lib.annotation.net.Headers;
import com.nuts.lib.annotation.net.Param;

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
}