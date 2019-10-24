package fi.hsl.features.splitdatabasetables.batchfiles.filewriters;

import org.springframework.batch.item.ItemWriter;

import java.io.IOException;

public class BufferedPipingCSVWriterFactory {
    public static ItemWriter<? super Object> createItemWriter() throws IOException {
        return new PipingCSVWriter(new BufferedWriterProviderWrapper(new FileWriterProvider()));
    }
}
