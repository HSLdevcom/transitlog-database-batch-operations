package fi.hsl.features.splitdatabasetables.batchfiles.filewriters;

import fi.hsl.domain.Event;

import java.io.IOException;
import java.io.Writer;

public interface WriterProvider {
    Writer provideFileWriter(Event item, FileWriterProvider.FilePath filePath) throws IOException;
}
