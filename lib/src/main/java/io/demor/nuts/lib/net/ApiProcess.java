package io.demor.nuts.lib.net;

public class ApiProcess {

    private NetBuilder mBuilder;

    public ApiProcess(final NetBuilder builder) {
        this.mBuilder = builder;
    }

    public NetResult execute() {
        return mBuilder.execute();
    }

}
