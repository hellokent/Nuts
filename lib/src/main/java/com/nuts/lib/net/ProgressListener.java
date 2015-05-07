package com.nuts.lib.net;

public interface ProgressListener {
    void transferred(long num, long total);
}
