package io.demor.nuts.lib.controller;

import io.demor.nuts.lib.annotation.net.Get;
import io.demor.nuts.lib.api.BaseResponse;

public interface TestApi2 {

    @Get("/get")
    BaseResponse get(int count);
}
