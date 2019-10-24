package fi.hsl.features.splitdatabasetables.batchfiles.filewriters;

import fi.hsl.domain.Event;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;

class BufferedWriterProviderWrapper extends WriterProviderDecorator {
    BufferedWriterProviderWrapper(WriterProvider writerProviderToDecorate) {
        super(writerProviderToDecorate);
    }

    @Override
    public Writer provideFileWriter(Event item, FileWriterProvider.FilePath filePath) throws IOException {
        Writer writer = super.provideFileWriter(item, filePath);
        BufferedWriter bufferedWriter = new BufferedWriter(writer);
        return bufferedWriter;
    }
}
