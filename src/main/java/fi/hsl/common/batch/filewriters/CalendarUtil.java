package fi.hsl.common.batch.filewriters;

import fi.hsl.domain.Event;

import java.sql.Timestamp;
import java.util.Calendar;

class CalendarUtil {
    static Calendar getCalendar(Event item) {
        long timestamp = item.getTst().getTime();
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp);
        return cal;
    }

    static Timestamp timeStampFor(int month, int day, int hour) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, hour);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);

        return new Timestamp(calendar.getTimeInMillis());
    }
}
