package fi.hsl.features.splitdatabasetables.batchfiles;

import fi.hsl.domain.Event;
import lombok.Data;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

class FileWriterProvider {

    private final Map<FilePath, Optional<FileName>> lastKnownFileName;

    private String VOLUME_PREFIX = "/csv/";

    FileWriterProvider() {
        this.lastKnownFileName = new ConcurrentHashMap<>();
    }

    FileWriter provideFileWriter(Event item, FilePath filePath) throws IOException {
        FileNameDateParts fileNameDateParts = createFileNameDateParts(item);
        Optional<FileName> fileName = lastKnownFileName.get(filePath);
        if (fileName != null && fileName.isPresent()) {
            return fileName.get().createAWriterOnFile();
        } else {
            FileName createdFile = new FileName(fileNameDateParts, filePath);
            lastKnownFileName.put(filePath, Optional.of(createdFile));
            return createdFile.createAWriterOnFile();
        }
    }

    private FileNameDateParts createFileNameDateParts(Event item) {
        Calendar calendar = getCalendar(item);
        return new FileNameDateParts(calendar);
    }

    private Calendar getCalendar(Event item) {
        long timestamp = item.getTst().getTime();
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp);
        return cal;
    }

    public enum FilePath {
        VEHICLE, OTHER, UNSIGNED, STOP, LIGHT
    }

    @Data
    private class FileName {
        private Integer day;
        private Integer month;
        private FilePath filePath;

        FileName(FileNameDateParts fileNameDateParts, FilePath filePath) {
            this.day = fileNameDateParts.day;
            this.month = fileNameDateParts.month;
            this.filePath = filePath;
        }

        FileWriter createAWriterOnFile() throws IOException {
            String absolutePath = generateAbsoluteFileName();
            String folderPath = generateFolderPath();

            File folder = new File(folderPath);
            folder.mkdirs();

            File absolutePathFile = new File(absolutePath);
            absolutePathFile.createNewFile();
            return new FileWriter(absolutePathFile, true);
        }

        private String generateFolderPath() {
            return VOLUME_PREFIX + filePath.toString();
        }

        String generateAbsoluteFileName() {
            return VOLUME_PREFIX + filePath.toString() + "/" + filePath.toString() + "_" + day + "_" + month + ".csv";
        }
    }

    private class FileNameDateParts {
        private Integer day;
        private Integer month;

        FileNameDateParts(Calendar calendar) {
            this.day = calendar.get(Calendar.DAY_OF_MONTH);
            this.month = calendar.get(Calendar.MONTH);
        }

    }
}
