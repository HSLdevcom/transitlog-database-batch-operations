package fi.hsl.common.batch.filewriters;

import fi.hsl.domain.Event;
import fi.hsl.domain.TableType;
import lombok.Data;

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
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

class FileWriterProvider implements WriterProvider {

    private final Map<TableType, Optional<FileName>> lastKnownFileName;

    private String VOLUME_PREFIX = "/csv/";

    FileWriterProvider() {
        this.lastKnownFileName = new ConcurrentHashMap<>();
    }

    @Override
    public Writer buildFileWriter(Event item) throws IOException {
        TableType tableType = item.getTableType();
        FileNameDateParts fileNameDateParts = createFileNameDateParts(item);
        Optional<FileName> fileName = lastKnownFileName.get(tableType);
        if (fileName != null && fileName.isPresent()) {
            FileName oldFileName = fileName.get();
            oldFileName.refreshIfNeeded(item);
            return new Writer(oldFileName.generateAbsoluteFileName(), oldFileName.createANIOWriterOnFile());
        } else {
            FileName createdFile = new FileName(fileNameDateParts, tableType);
            lastKnownFileName.put(tableType, Optional.of(createdFile));
            BufferedWriter newWriterOnFile = createdFile.createANIOWriterOnFile();
            return new Writer(createdFile.generateAbsoluteFileName(), newWriterOnFile);
        }
    }

    private FileNameDateParts createFileNameDateParts(Event item) {
        Calendar calendar = CalendarUtil.getCalendar(item);
        return new FileNameDateParts(calendar);
    }

    @Data
    private class FileName {
        private Integer hour;
        private Integer day;
        private Integer month;
        private TableType tableType;

        FileName(FileNameDateParts fileNameDateParts, TableType tableType) {
            this.day = fileNameDateParts.day;
            this.month = fileNameDateParts.month;
            this.hour = fileNameDateParts.hour;

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

        void refreshIfNeeded(Event item) {
            Calendar calendar = CalendarUtil.getCalendar(item);
            this.day = calendar.get(Calendar.DAY_OF_MONTH);
            this.month = calendar.get(Calendar.MONTH);
            this.hour = calendar.get(Calendar.HOUR);
        }
    }

    public class FileNameDateParts {
        private Integer day;
        private Integer month;
        private Integer hour;

        FileNameDateParts(Calendar calendar) {
            this.day = calendar.get(Calendar.DAY_OF_MONTH);
            this.month = calendar.get(Calendar.MONTH);
            this.hour = calendar.get(Calendar.HOUR_OF_DAY);
        }

    }

}
