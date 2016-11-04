package io.demor.nuts.lib.api;

import io.demor.nuts.lib.annotation.net.*;
import io.demor.nuts.lib.net.ParamList;
import io.demor.nuts.lib.net.ProgressListener;

import java.io.File;
import java.util.Map;

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

    @Get("paramList")
    BaseResponse testParamList(ParamList list1, @Param("invalid") ParamList list2);

    @Get("map")
    BaseResponse testMap(Map<Integer, Long> map);

    @Get("map2")
    BaseResponse testMap2(Map<String, Integer> map, ParamList list, @Param("param") String p);

    @Get("retry")
    @Retry(3)
    BaseResponse retry();

    @Multipart("upload")
    BaseResponse uploadFile(@Param("file") File file, @Param("file") ProgressListener listener);
}