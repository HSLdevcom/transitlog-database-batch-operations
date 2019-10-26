package fi.hsl.common.batch;

import fi.hsl.common.batch.filewriters.WriterProvider;

import java.io.IOException;

import static fi.hsl.common.batch.filewriters.CachedWriterFactory.createFileWriterProvider;

public class CSVWriterFactory {
    public static CSVItemWriter createCSVItemWriter() throws IOException {
        WriterProvider fileWriterProvider = createFileWriterProvider();
        return new CSVItemWriter(fileWriterProvider);
    }
}
