package io.demor.nuts.lib;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 线程安全的SimpleDateFormat
 */
public class ThreadSafeDateFormat extends DateFormat {

    private final String mPattern;
    private final ThreadLocal<SimpleDateFormat> mFormatThreadLocal = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat(mPattern);
        }
    };

    public ThreadSafeDateFormat(final String pattern) {
        mPattern = pattern;
    }

    public String getPattern() {
        return mPattern;
    }

    @Override
    public StringBuffer format(final Date date, final StringBuffer buffer, final FieldPosition field) {
        return mFormatThreadLocal.get().format(date, buffer, field);
    }

    @Override
    public Date parse(final String string, final ParsePosition position) {
        return mFormatThreadLocal.get().parse(string, position);
    }
}
