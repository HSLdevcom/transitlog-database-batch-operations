package fi.hsl.features.splitdatabasetables.batch.filewriters;

import fi.hsl.domain.Event;
import fi.hsl.domain.TableType;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.io.Writer;
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
    public Writer provideFileWriter(Event item, TableType eventType) throws IOException {
        WriterContext writerContext = latestKnownWriters.get(eventType);
        Calendar calendar = CalendarUtil.getCalendar(item);
        int eventMonth = calendar.get(Calendar.MONTH);
        int eventDay = calendar.get(Calendar.DAY_OF_MONTH);

        //Return a new file writer if needed
        if (writerContext == null) {
            //Provide a new writer for this file path
            return instantiateNewWriter(item, eventType, eventMonth, eventDay);
        }
        WriterContext latestFilePathWriter = latestKnownWriters.get(eventType);

        if (latestFilePathWriter.cantBeReused(eventDay, eventMonth)) {
            return instantiateNewWriter(item, eventType, eventMonth, eventDay);
        }
        //Reuse an old writer
        return latestFilePathWriter.writer;

    }

    private Writer instantiateNewWriter(Event item, TableType tableType, int eventMonth, int eventDay) throws IOException {
        Writer instantiatedWriter = super.provideFileWriter(item, tableType);
        latestKnownWriters.put(tableType, new WriterContext(instantiatedWriter, eventDay, eventMonth));
        return instantiatedWriter;
    }


    @AllArgsConstructor
    private class WriterContext {
        private Writer writer;
        private int day;
        private int month;

        boolean cantBeReused(int eventDay, int eventMonth) throws IOException {
            if (isClosed()) {
                return true;
            }
            if (isOld(eventDay, eventMonth)) {
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

        private boolean isOld(int eventDay, int eventMonth) {
            return day != eventDay && month != eventMonth;
        }
    }
}
