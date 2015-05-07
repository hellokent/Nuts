package com.nuts.lib;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 线程安全的SimpleDateFormat
 */
public class ThreadSafeDateFormat extends DateFormat {

    final ThreadLocal<SimpleDateFormat> mFormatThreadLocal = new ThreadLocal<>();
    final String mPattern;

    public ThreadSafeDateFormat(final String pattern) {
        mPattern = pattern;
    }

    @Override
    public StringBuffer format(final Date date, final StringBuffer buffer, final FieldPosition field) {
        return getFormat().format(date, buffer, field);
    }

    @Override
    public Date parse(final String string, final ParsePosition position) {
        return getFormat().parse(string, position);
    }

    public synchronized SimpleDateFormat getFormat() {
        final SimpleDateFormat format;
        if (mFormatThreadLocal.get() == null) {
            format = new SimpleDateFormat(mPattern, Locale.getDefault());
            mFormatThreadLocal.set(format);
        } else {
            format = mFormatThreadLocal.get();
        }
        return format;
    }
}
