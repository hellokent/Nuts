package io.demor.nuts.lib.api;

import io.demor.nuts.lib.annotation.net.Get;
import io.demor.nuts.lib.annotation.net.Param;
import rx.Observable;

public interface TestApi {

    @Get("test")
    Observable<BaseResponse> test(@Param("a") String a, @Param("b") String b);
}