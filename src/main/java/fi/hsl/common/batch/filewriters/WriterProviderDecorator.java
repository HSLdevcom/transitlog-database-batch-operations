package fi.hsl.common.batch.filewriters;

import fi.hsl.domain.Event;

import java.io.IOException;

abstract class WriterProviderDecorator implements WriterProvider {
    private final WriterProvider writerProviderToDecorate;

    WriterProviderDecorator(WriterProvider writerProviderToDecorate) {
        this.writerProviderToDecorate = writerProviderToDecorate;
    }

    @Override
    public Writer buildFileWriter(Event item) throws IOException {
        return writerProviderToDecorate.buildFileWriter(item);
    }
}
