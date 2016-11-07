package io.demor.nuts.lib.storage;

public interface IStorageEngine {

    void set(String key, String value);

    String get(String key);

    void delete(String key);

    boolean contains(String key);
}
