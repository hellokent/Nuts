package io.demor.nuts.lib.api;

import io.demor.nuts.lib.annotation.net.*;
import io.demor.nuts.lib.net.ProgressListener;
import rx.Observable;

import java.io.File;

public interface TestApi {

    @Get("test")
    Observable<BaseResponse> test(@Param("a") String a, @Param("b") String b);

    @Get("url")
    Observable<BaseResponse> testUrl();

    @Get("header")
    @Headers({"r1:h1", "r2:h2"})
    Observable<BaseResponse> header();

    @Get("header")
    Observable<BaseResponse> header1(@Header("p1") String header);

    @Get("%s/user")
    Observable<BaseResponse> testParamUrl(@Path String id);

    @Multipart("upload")
    Observable<BaseResponse> uploadFile(@Param("file") File file, @Param("file") ProgressListener listener);
}