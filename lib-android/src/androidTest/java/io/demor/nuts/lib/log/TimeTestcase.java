package io.demor.nuts.lib.log;

import junit.framework.TestCase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeTestcase extends TestCase {

    public void testTime() throws Exception {
        final LogTime time = new LogTime();
        long timeInMillis = System.currentTimeMillis();

        Date date = new Date();
        date.setTime(timeInMillis);
        assertEquals(new SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault()).format(date), time.toString());
    }

    public void testDay() throws Exception {
        final LogDay day = new LogDay();
        long timeInMillis = System.currentTimeMillis();

        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        date.setTime(timeInMillis);
        assertEquals(format.format(date), day.toString());
    }

    public void testStringArray() throws Exception {
        final Logger logger = LoggerFactory.getLogger();
        for (int i = 0; i < BaseLogTime.NUMBER_ARRAY.length; ++i) {
            logger.d("begin test:%s", i);
            assertNotNull(BaseLogTime.NUMBER_ARRAY[i]);
            assertEquals(i, Integer.parseInt(BaseLogTime.NUMBER_ARRAY[i]));
        }

    }
}
