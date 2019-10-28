package fi.hsl.common.batch;

import java.io.IOException;

import static fi.hsl.common.batch.filewriters.FileWriterFactory.createFileWriterProvider;

public class CSVWriterFactory {
    public static CSVItemWriter createCSVItemWriter() throws IOException {
        return new CSVItemWriter(createFileWriterProvider());
    }
}
