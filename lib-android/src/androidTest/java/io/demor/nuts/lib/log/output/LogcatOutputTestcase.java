package io.demor.nuts.lib.log.output;

import android.test.AndroidTestCase;
import android.util.Log;
import io.demor.nuts.lib.log.LogContext;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;

public class LogcatOutputTestcase extends AndroidTestCase {

    private int mLevel;
    private String mText;
    private String mTag;

    private Element mRoot;

    @Override
    public void setUp() throws Exception {
        mRoot = DocumentBuilderFactory
                .newInstance()
                .newDocumentBuilder()
                .parse(getContext().getAssets().open("logcat_test.xml"))
                .getDocumentElement();
    }

    public void testTag() throws Exception {
        NodeList outputs = mRoot.getElementsByTagName("output");

        TestLogcatLogger logcatLogger = new TestLogcatLogger((Element) outputs.item(0));

        LogContext context = new LogContext();
        context.mLevel = Log.VERBOSE;
        context.mTag = "aaa";
        context.mMsg = "msg";

        logcatLogger.append(context);

        assertEquals(mLevel, context.mLevel);
        assertEquals(mTag, context.mTag);
        assertEquals(mText, context.mTag + ":" + context.mMsg);
    }

    @Override
    public void tearDown() throws Exception {
        mLevel = -1;
        mTag = mText = "";
    }

    private class TestLogcatLogger extends LogcatOutput {

        TestLogcatLogger(Element element) throws DOMException {
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
