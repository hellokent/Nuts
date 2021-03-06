package io.demor.nuts.lib.storage;

import com.google.common.base.Joiner;
import com.google.common.io.Files;
import io.demor.nuts.lib.log.Logger;
import io.demor.nuts.lib.log.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

public class FileEngine implements IStorageEngine {

    static final Logger LOGGER = LoggerFactory.getLogger(FileEngine.class);
    protected File mFolder;

    protected FileEngine() {
    }

    public FileEngine(File folder) {
        mFolder = folder;
        if (!mFolder.exists()) {
            mFolder.mkdirs();
        }
    }

    @Override
    public void set(final String key, final String value) {
        try {
            Files.write(value.getBytes(), new File(mFolder, key));
        } catch (IOException e) {
            LOGGER.e("error in write file(%s). %s", key, e.getMessage());
        }
    }

    @Override
    public String get(final String key) {
        try {
            return Joiner.on("")
                    .join(Files.asCharSource(new File(mFolder, key), Charset.defaultCharset())
                            .readLines());
        } catch (IOException e) {
            LOGGER.e("error in read file(%s), %s", key, e.getMessage());
            return "";
        }
    }

    @Override
    public void delete(final String key) {
        new File(mFolder, key).delete();
    }

    @Override
    public boolean contains(final String key) {
        return new File(mFolder, key).exists();
    }
}
