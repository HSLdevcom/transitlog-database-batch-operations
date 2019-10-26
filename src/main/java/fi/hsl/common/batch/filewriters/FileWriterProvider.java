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
    public BufferedWriter provideFileWriter(Event item, TableType tableType) throws IOException {
        FileNameDateParts fileNameDateParts = createFileNameDateParts(item);
        Optional<FileName> fileName = lastKnownFileName.get(tableType);
        if (fileName != null && fileName.isPresent()) {
            return fileName.get().createANIOWriterOnFile();
        } else {
            FileName createdFile = new FileName(fileNameDateParts, tableType);
            lastKnownFileName.put(tableType, Optional.of(createdFile));
            return createdFile.createANIOWriterOnFile();
        }
    }

    private FileNameDateParts createFileNameDateParts(Event item) {
        Calendar calendar = CalendarUtil.getCalendar(item);
        return new FileNameDateParts(calendar);
    }

    @Data
    private class FileName {
        private final Integer hour;
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
