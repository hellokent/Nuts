package com.nuts.test.controller;

import com.nuts.lib.annotation.net.Get;
import com.nuts.test.api.BaseResponse;

public interface TestApi2 {

    @Get("/get")
    BaseResponse get(int count);
}
