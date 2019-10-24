package fi.hsl.features.splitdatabasetables.batchfiles.filewriters;

import fi.hsl.domain.Event;

import java.io.IOException;
import java.io.Writer;

abstract class WriterProviderDecorator implements WriterProvider {
    private final WriterProvider writerProviderToDecorate;

    WriterProviderDecorator(WriterProvider writerProviderToDecorate) {
        this.writerProviderToDecorate = writerProviderToDecorate;
    }

    @Override
    public Writer provideFileWriter(Event item, FileWriterProvider.FilePath filePath) throws IOException {
        return writerProviderToDecorate.provideFileWriter(item, filePath);
    }
}
