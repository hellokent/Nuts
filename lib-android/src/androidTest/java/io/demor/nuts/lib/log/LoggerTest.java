package io.demor.nuts.lib.log;

import android.content.res.AssetManager;
import android.test.AndroidTestCase;
import android.util.Log;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.xml.parsers.DocumentBuilderFactory;

import io.demor.nuts.lib.log.output.LogcatOutput;

public class LoggerTest extends AndroidTestCase {

    private Logger mLogger = new Logger("path", "tag");

    private Element mRoot;

    private int mLevel;
    private String mTag;
    private String mText;

    @Override
    public void setUp() throws Exception {
        final AssetManager assetManager = getContext().getAssets();
        mRoot = DocumentBuilderFactory
                .newInstance()
                .newDocumentBuilder()
                .parse(assetManager.open("logcat_test.xml"))
                .getDocumentElement();
    }

    public void testLevel() throws Exception {
        mLogger.mLogOutputs.add(new TestOutput((Element) mRoot.getElementsByTagName("output").item(1)));

        mLogger.d("test");
        Thread.sleep(100);
        assertEquals(Log.DEBUG, mLevel);
        assertEquals("tag:test", mText);
        assertEquals("tag", mTag);

        mLogger.v("test");
        Thread.sleep(100);
        assertEquals(Log.VERBOSE, mLevel);

        mLogger.i("test");
        Thread.sleep(100);
        assertEquals(Log.INFO, mLevel);

        mLogger.w("test");
        Thread.sleep(100);
        assertEquals(Log.WARN, mLevel);

        mLogger.e("test");
        Thread.sleep(100);
        assertEquals(Log.ERROR, mLevel);
    }

    public void testThreadId() throws Exception {
        final String logText = "asdf";
        mLogger.mLogOutputs.add(new LogcatOutput((Element) mRoot.getElementsByTagName("output").item(2)));

        final Executor executor = Executors.newFixedThreadPool(20);
        final int times = 100;
        final CountDownLatch latch = new CountDownLatch(times);

        for (int i = 0; i < times; ++i) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    LogContext logContext = mLogger.getLogContext();
                    assertEquals(Thread.currentThread(), logContext.mCurrentThread);
                    latch.countDown();
                }
            });
        }
        latch.await();
    }

    @Override
    public void tearDown() throws Exception {
        mLogger.mLogOutputs.clear();
        mLogger.mLocalLogContext.remove();
    }

    private class TestOutput extends LogcatOutput {

        private TestOutput(Element element) throws DOMException {
            super(element);
        }

        @Override
        protected void log(int level, String tag, String text) {
            mLevel = level;
            mTag = tag;
            mText = text;
        }
    }

}
