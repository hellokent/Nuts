package io.demor.nuts.common.server;

public class MultiPathLoadedException extends RuntimeException {
    public MultiPathLoadedException(String loadedPath) {
        super(" \"" + loadedPath + "\" loaded");
    }
}
