package fi.hsl.common.batch.filewriters;

import java.io.IOException;


/**
 * Returns a new cached and non-blocking CSV ItemWriter for the running chunk
 * backed by Java NIO Api .
 * <p>
 * Splits the
 */
public class CachedWriterFactory {
    public static WriterProvider createFileWriterProvider() throws IOException {
        FileWriterProvider fileWriterProvider = new FileWriterProvider();
        return new CachedWriterProvider(fileWriterProvider);
    }
}
