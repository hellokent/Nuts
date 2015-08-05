package com.nuts.test.api;

import com.nuts.lib.annotation.net.Get;
import com.nuts.lib.annotation.net.Param;

public interface MockApi {

    @Get("test")
    BaseResponse test(@Param("a") String a, @Param("b") String b);
}
