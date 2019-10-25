package fi.hsl.features.splitdatabasetables.batch.filewriters;

import fi.hsl.domain.Event;
import fi.hsl.domain.TableType;

import java.io.IOException;
import java.io.Writer;

public interface WriterProvider {
    Writer provideFileWriter(Event item, TableType tableType) throws IOException;

}
