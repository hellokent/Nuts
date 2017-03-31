package io.demor.nuts.lib.log;

import java.util.Calendar;

public class BaseLogTime {

    static final String[] NUMBER_ARRAY;

    static {
        NUMBER_ARRAY = new String[1000];
        for (int i = 0; i <= 9; ++i) {
            NUMBER_ARRAY[i] = "0" + String.valueOf(i);
        }
        for (int i = 10; i <= 999; ++i) {
            NUMBER_ARRAY[i] = String.valueOf(i);
        }
    }

    protected Calendar mCalendar;

    public BaseLogTime() {
        mCalendar = Calendar.getInstance();
        updateTime(mCalendar.getTimeInMillis());
    }

    public void updateTime(long millis) {
        mCalendar.setTimeInMillis(millis);
    }

}
