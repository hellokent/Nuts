package io.demor.nuts.lib.logger.output;

import android.app.Application;
import android.os.Handler;
import android.os.HandlerThread;
import io.demor.nuts.lib.logger.LogContext;
import io.demor.nuts.lib.logger.LogFormatter;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.FileWriter;
import java.io.IOException;

public class FileOutput extends LogcatOutput {

    private static final Handler FILE_WRITE_HANDLER;

    static {
        final HandlerThread thread = new HandlerThread("file-output");
        thread.start();
        FILE_WRITE_HANDLER = new Handler(thread.getLooper());
    }

    private final LogFormatter<LogFileContext> mPathFormatter;
    private LogFileContext mLogFileContext;
    private FileWriter mWriter;
    private String mPath;

    public FileOutput(Application application, Element element) throws DOMException {
        super(element);
        final NodeList pathNodeList = element.getElementsByTagName("path");
        if (pathNodeList.getLength() == 0) {
            throw new Error("File output node must have path node");
        }
        Element pathElement = (Element) pathNodeList.item(0);
        mPathFormatter = new LogFormatter<>(pathElement.getTextContent(), LogFileContext.class);
        mLogFileContext = new LogFileContext(application);
    }

    @Override
    public void append(final LogContext context) {
        mLogFileContext.mDay.updateTime(System.currentTimeMillis());
        final String currentPath = mPathFormatter.format(mLogFileContext);

        FILE_WRITE_HANDLER.post(new Runnable() {
            @Override
            public void run() {
                if (!currentPath.equals(mPath) && mWriter != null) {
                    try {
                        mWriter.flush();
                    } catch (IOException ignored) {
                    }
                    try {
                        mWriter.close();
                    } catch (IOException ignored) {
                    }
                }
                mPath = currentPath;
                if (mWriter == null) {
                    try {
                        mWriter = new FileWriter(currentPath);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return;
                    }
                }
                try {
                    mWriter.write(mFormatter.format(context));
                    mWriter.write("\r\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
