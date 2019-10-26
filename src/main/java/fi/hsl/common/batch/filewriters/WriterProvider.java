package fi.hsl.common.batch.filewriters;

import fi.hsl.domain.Event;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.IOException;

public interface WriterProvider {
    Writer buildFileWriter(Event item) throws IOException;

    @AllArgsConstructor
    @Data
    class Writer {
        private String writerTarget;
        private java.io.Writer writerOnFile;

        void close() throws IOException {
            writerOnFile.flush();
            writerOnFile.close();
        }

        void flush() throws IOException {
            writerOnFile.flush();
        }
    }
}
