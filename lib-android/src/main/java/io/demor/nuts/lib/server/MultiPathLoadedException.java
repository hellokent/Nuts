package io.demor.nuts.lib.server;

public class MultiPathLoadedException extends RuntimeException {
    public MultiPathLoadedException(String loadedPath) {
        super(" \"" + loadedPath + "\" loaded");
    }
}
