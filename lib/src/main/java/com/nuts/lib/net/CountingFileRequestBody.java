package com.nuts.lib.net;

import java.io.File;
import java.io.IOException;

import com.nuts.lib.Globals;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.internal.Util;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

class CountingFileRequestBody extends RequestBody {

    private static final int SEGMENT_SIZE = 2048; // okio.Segment.SIZE

    private final File mFile;

    private final ProgressListener mListener;

    private final MediaType mContentType;

    private final long mTotalLength;

    public CountingFileRequestBody(MediaType contentType, File file, ProgressListener listener) {
        mFile = file;
        mContentType = contentType;
        mListener = listener;
        mTotalLength = file.length();
    }

    @Override
    public long contentLength() {
        return mFile.length();
    }

    @Override
    public MediaType contentType() {
        return mContentType;
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        Source source = null;
        try {
            source = Okio.source(mFile);
            long transferred = 0;
            long read;

            while ((read = source.read(sink.buffer(), SEGMENT_SIZE)) != -1) {
                transferred += read;
                sink.flush();
                final long uiTransferred = transferred;
                Globals.UI_HANDLER.post(new Runnable() {
                    @Override
                    public void run() {
                        mListener.transferred(uiTransferred, mTotalLength);
                    }
                });
            }
        } finally {
            Util.closeQuietly(source);
        }
    }

}