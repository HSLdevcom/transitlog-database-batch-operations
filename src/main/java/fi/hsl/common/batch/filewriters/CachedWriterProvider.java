package fi.hsl.common.batch.filewriters;

import fi.hsl.domain.Event;
import fi.hsl.domain.TableType;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class CachedWriterProvider extends WriterProviderDecorator {
    private final Map<TableType, WriterContext> latestKnownWriters;

    CachedWriterProvider(WriterProvider writerProviderToDecorate) {
        super(writerProviderToDecorate);
        this.latestKnownWriters = new ConcurrentHashMap<>();
    }

    @Override
    public Writer buildFileWriter(Event item) throws IOException {
        WriterContext writerContext = latestKnownWriters.get(item.getTableType());
        Calendar calendar = CalendarUtil.getCalendar(item);
        int eventMonth = calendar.get(Calendar.MONTH);
        int eventDay = calendar.get(Calendar.DAY_OF_MONTH);
        int eventHour = calendar.get(Calendar.HOUR);

        //Return a new file writer if needed
        if (writerContext == null) {
            //Provide a new writer for this file path
            return instantiateNewWriter(item, item.getTableType(), eventMonth, eventDay, eventHour);
        }
        WriterContext latestFilePathWriter = latestKnownWriters.get(item.getTableType());

        if (latestFilePathWriter.cantBeReused(eventDay, eventMonth, eventHour)) {
            return instantiateNewWriter(item, item.getTableType(), eventMonth, eventDay, eventHour);
        }
        //Reuse an old writer
        return latestFilePathWriter.writer;

    }

    private Writer instantiateNewWriter(Event item, TableType tableType, int month, int day, int hour) throws IOException {
        Writer instantiatedWriter = super.buildFileWriter(item);
        latestKnownWriters.put(tableType, new WriterContext(instantiatedWriter, month, day, hour));
        return instantiatedWriter;
    }


    @AllArgsConstructor
    private class WriterContext {
        private Writer writer;
        private int day;
        private int month;
        private int hour;

        boolean cantBeReused(int eventDay, int eventMonth, int eventHour) throws IOException {
            if (isClosed()) {
                return true;
            }
            if (isOld(eventDay, eventMonth, eventHour)) {
                //Remember to close the writer to avoid clogging up JVM
                writer.close();
                return true;
            }
            return false;
        }

        private boolean isClosed() {
            try {
                writer.flush();
            } catch (IOException e) {
                return true;
            }
            return false;
        }

        private boolean isOld(int eventDay, int eventMonth, int eventHour) {
            return day != eventDay || month != eventMonth || hour != eventHour;
        }
    }
}
