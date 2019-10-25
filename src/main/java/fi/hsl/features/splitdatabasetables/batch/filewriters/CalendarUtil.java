package fi.hsl.features.splitdatabasetables.batch.filewriters;

import fi.hsl.domain.Event;

import java.util.Calendar;

class CalendarUtil {
    static Calendar getCalendar(Event item) {
        long timestamp = item.getTst().getTime();
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp);
        return cal;
    }
}
