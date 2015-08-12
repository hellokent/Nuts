package io.demor.nuts.test.controller;

import io.demor.nuts.lib.annotation.net.Get;
import io.demor.nuts.test.api.BaseResponse;

public interface TestApi2 {

    @Get("/get")
    BaseResponse get(int count);
}
