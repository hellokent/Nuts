package io.demor.nuts.lib.log;

import java.util.Calendar;

public class LogDay extends BaseLogTime {

    private String mFormattedString;

    @Override
    public void updateTime(long millis) {
        super.updateTime(millis);
        mFormattedString = "20" + NUMBER_ARRAY[mCalendar.get(Calendar.YEAR) - 2000] + "-"
                + NUMBER_ARRAY[mCalendar.get(Calendar.MONTH) + 1] + "-"
                + NUMBER_ARRAY[mCalendar.get(Calendar.DAY_OF_MONTH)];
    }

    @Override
    public String toString() {
        return mFormattedString;
    }
}
