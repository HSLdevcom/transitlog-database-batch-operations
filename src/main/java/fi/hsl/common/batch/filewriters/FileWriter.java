package fi.hsl.common.batch.filewriters;

import fi.hsl.domain.Event;
import fi.hsl.domain.TableType;
import lombok.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class FileWriter {
    private String VOLUME_PREFIX = "/csv/";
    private Map<DatePath, WriterContext> previousContext = new ConcurrentHashMap<>();

    private Calendar getCalendar(Event item) {
        long timestamp = item.getTst().getTime();
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp);
        return cal;
    }

    public synchronized WriterContext buildFileWriterContext(Event item) throws IOException {
        DatePath key = new DatePath(item);
        if (previousContext.containsKey(key)) {
            WriterContext writerContext = previousContext.get(key);
            if (writerContext.isAvailable()) {
                return writerContext;
            } else {
                return cacheNewContext(item, key);
            }
        }
        return cacheNewContext(item, key);
    }

    private WriterContext cacheNewContext(Event item, DatePath key) throws IOException {
        WriterContext newWriterContext = createNewWriterContext(item);
        previousContext.put(key, newWriterContext);
        return newWriterContext;
    }

    private WriterContext createNewWriterContext(Event item) throws IOException {
        DatePath datePathParts = createFileNameDateParts(item);
        FileName createdFile = new FileName(datePathParts, item.getTableType());
        BufferedWriter newWriterOnFile = createdFile.createANIOWriterOnFile();
        return new WriterContext(newWriterOnFile, createdFile.generateAbsoluteFileName());
    }

    private DatePath createFileNameDateParts(Event item) {
        return new DatePath(item);
    }

    @Data
    private class FileName {
        private Integer hour;
        private Integer day;
        private Integer month;
        private TableType tableType;

        FileName(DatePath fileNameDatePathParts, TableType tableType) {
            this.day = fileNameDatePathParts.day;
            this.month = fileNameDatePathParts.month;
            this.hour = fileNameDatePathParts.hour;
            this.tableType = tableType;
        }

        BufferedWriter createANIOWriterOnFile() throws IOException {
            String absolutePath = generateAbsoluteFileName();
            String folderPath = generateFolderPath();

            File folder = new File(folderPath);
            folder.mkdirs();

            File absolutePathFile = new File(absolutePath);
            absolutePathFile.createNewFile();

            Path path = Paths.get(absolutePathFile.toURI());
            return Files.newBufferedWriter(path, Charset.defaultCharset(), StandardOpenOption.APPEND);
        }

        private String generateFolderPath() {
            return VOLUME_PREFIX + tableType.toString();
        }

        String generateAbsoluteFileName() {
            return VOLUME_PREFIX + tableType.toString() + "/" + tableType.toString() + "_" + day + "_" + month + "_" + hour + ".csv";
        }
    }


    @AllArgsConstructor
    public class WriterContext {
        @Getter
        private final BufferedWriter fileWriter;
        @Getter
        private final String generatedAbsoluteFileName;

        boolean isAvailable() {
            try {
                fileWriter.flush();
            } catch (IOException e) {
                return false;
            }
            return true;
        }

    }

    @EqualsAndHashCode
    private class DatePath {
        private final int month;
        private final int day;
        private final int hour;
        private TableType tableType;

        DatePath(Event item) {
            Calendar calendar = getCalendar(item);
            this.tableType = item.getTableType();
            this.month = calendar.get(Calendar.MONTH);
            this.day = calendar.get(Calendar.DAY_OF_MONTH);
            this.hour = calendar.get(Calendar.HOUR_OF_DAY);
        }
    }
}

