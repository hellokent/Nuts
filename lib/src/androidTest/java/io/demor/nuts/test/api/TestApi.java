package io.demor.nuts.test.api;

import java.io.File;

import io.demor.nuts.lib.annotation.net.Get;
import io.demor.nuts.lib.annotation.net.Header;
import io.demor.nuts.lib.annotation.net.Headers;
import io.demor.nuts.lib.annotation.net.Multipart;
import io.demor.nuts.lib.annotation.net.Param;
import io.demor.nuts.lib.annotation.net.Path;
import io.demor.nuts.lib.net.ProgressListener;

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