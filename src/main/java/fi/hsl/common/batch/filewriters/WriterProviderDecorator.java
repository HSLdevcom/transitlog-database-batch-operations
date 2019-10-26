package fi.hsl.common.batch.filewriters;

import fi.hsl.domain.Event;
import fi.hsl.domain.TableType;

import java.io.IOException;
import java.io.Writer;

abstract class WriterProviderDecorator implements WriterProvider {
    private final WriterProvider writerProviderToDecorate;

    WriterProviderDecorator(WriterProvider writerProviderToDecorate) {
        this.writerProviderToDecorate = writerProviderToDecorate;
    }

    @Override
    public Writer provideFileWriter(Event item, TableType tableType) throws IOException {
        return writerProviderToDecorate.provideFileWriter(item, tableType);
    }
}
