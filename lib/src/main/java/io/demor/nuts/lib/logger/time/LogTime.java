package io.demor.nuts.lib.logger.time;

import java.util.Calendar;

public class LogTime extends BaseLogTime {

    private String mFormattedString;

    @Override
    public void updateTime(long millis) {
        super.updateTime(millis);
        mFormattedString = NUMBER_ARRAY[mCalendar.get(Calendar.HOUR_OF_DAY)] + ":"
                + NUMBER_ARRAY[mCalendar.get(Calendar.MINUTE)] + ":"
                + NUMBER_ARRAY[mCalendar.get(Calendar.SECOND)] + "."
                + NUMBER_ARRAY[mCalendar.get(Calendar.MILLISECOND)];
    }

    @Override
    public String toString() {
        return mFormattedString;
    }
}
