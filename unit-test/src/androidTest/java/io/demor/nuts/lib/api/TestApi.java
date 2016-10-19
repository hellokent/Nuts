package io.demor.nuts.lib.api;

import com.google.common.base.Strings;
import io.demor.nuts.common.server.annotation.Request;
import io.demor.nuts.common.server.annotation.Url;

@Url("test")
public class TestApi {

    @Url("foo")
    public String foo(@Request("arg1") String arg1, @Request(value = "arg2", required = false) String arg2) {
        return "foo:" + Strings.nullToEmpty(arg1) + Strings.nullToEmpty(arg2);
    }

    @Url("/bar")
    public String bar() {
        return "bar";
    }
}
