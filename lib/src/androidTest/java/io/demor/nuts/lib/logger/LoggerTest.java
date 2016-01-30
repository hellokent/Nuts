package io.demor.nuts.lib.logger;

import android.content.res.AssetManager;
import android.test.AndroidTestCase;
import android.util.Log;
import com.google.common.base.Splitter;
import io.demor.nuts.lib.logger.output.LogcatOutput;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

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
        assertEquals(Log.DEBUG, mLevel);
        assertEquals("tag:test", mText);
        assertEquals("tag", mTag);

        mLogger.v("test");
        assertEquals(Log.VERBOSE, mLevel);

        mLogger.i("test");
        assertEquals(Log.INFO, mLevel);

        mLogger.w("test");
        assertEquals(Log.WARN, mLevel);

        mLogger.e("test");
        assertEquals(Log.ERROR, mLevel);
    }

    public void testThreadId() throws Exception {
        mLogger.mLogOutputs.add(new LogcatOutput((Element) mRoot.getElementsByTagName("output").item(2)) {
            @Override
            protected void log(int level, String tag, String text) {
                final Thread curThread = Thread.currentThread();
                List<String> list = Splitter.on(":").splitToList(text);
                assertEquals(String.valueOf(curThread.getId()), list.get(0));
                assertEquals(curThread.getName(), list.get(1));
            }
        });

        final Executor executor = Executors.newFixedThreadPool(20);
        final int times = 100000;
        final CountDownLatch latch = new CountDownLatch(times);

        for (int i = 0; i < times; ++i) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    mLogger.v("asdf");
                    latch.countDown();
                }
            });
        }
        latch.await();
    }

    public void testMethodProfile() throws Exception {
        mLogger.mLogOutputs.add(new LogcatOutput((Element) mRoot.getElementsByTagName("output").item(2)));
        final int times = 100000;

        final long startTime = System.currentTimeMillis();
        for (int i = 0; i < times; ++i) {
            mLogger.v("asdf");
        }
        final long usedTimeMillis = System.currentTimeMillis() - startTime;
        Log.v("log", String.format("used:%dms. %f/s", usedTimeMillis, (times / (usedTimeMillis / 1000f))));

    }

    @Override
    public void tearDown() throws Exception {
        mLogger.mLogOutputs.clear();
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
