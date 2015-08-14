package io.demor.nuts.test.storage;

import android.test.AndroidTestCase;

import io.demor.nuts.lib.storage.FileEngine;
import io.demor.nuts.lib.storage.IStorageEngine;
import io.demor.nuts.lib.storage.MemoryEngine;
import io.demor.nuts.lib.storage.SharedPreferenceStorageEngine;

public class StorageEngineTestCase extends AndroidTestCase {

    @Override
    public void setUp() throws Exception {
    }

    public void testSp() throws Exception {
        testEngine(new SharedPreferenceStorageEngine(getContext()));
    }

    public void testFile() throws Exception {
        testEngine(new FileEngine(getContext()));
    }

    public void testMemory() throws Exception {
        testEngine(new MemoryEngine());
    }

    private void testEngine(IStorageEngine engine) {
        engine.set("key", "value");
        assertEquals("value", engine.get("key"));
        assertTrue(engine.contains("key"));
        engine.delete("key");
        assertFalse(engine.contains("key"));
    }

}
