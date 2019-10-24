package fi.hsl.features.splitdatabasetables.batchfiles;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import fi.hsl.domain.*;
import org.springframework.batch.item.ItemWriter;

import java.io.IOException;
import java.util.List;

import static fi.hsl.features.splitdatabasetables.batchfiles.FileWriterProvider.FilePath.*;

public class PipingCSVWriter implements ItemWriter<Object> {

    private final CsvMapper csvMapper;
    private final CsvSchema vehiclePositionSchema;
    private final CsvSchema stopEventSchema;
    private final CsvSchema otherEventSchema;
    private final CsvSchema unsignedEventSchema;
    private final CsvSchema lightPriorityEventSchema;
    private final FileWriterProvider fileWriterProvider;

    PipingCSVWriter(FileWriterProvider fileWriterProvider) throws IOException {

        this.fileWriterProvider = fileWriterProvider;
        csvMapper = new CsvMapper();

        //Open file system hooks first

        lightPriorityEventSchema = csvMapper.schemaFor(LightPriorityEvent.class);
        unsignedEventSchema = csvMapper.schemaFor(UnsignedEvent.class);
        otherEventSchema = csvMapper.schemaFor(OtherEvent.class);
        stopEventSchema = csvMapper.schemaFor(StopEvent.class);
        vehiclePositionSchema = csvMapper.schemaFor(VehiclePosition.class);

    }

    @Override
    public void write(List<?> items) throws Exception {
        items.stream()
                .forEach(item -> {
                    try {
                        if (item instanceof VehiclePosition) {
                            csvMapper.writer(vehiclePositionSchema).writeValue(fileWriterProvider.provideFileWriter((Event) item, VEHICLE), item);
                            return;
                        }
                        if (item instanceof StopEvent) {
                            csvMapper.writer(stopEventSchema).writeValue(fileWriterProvider.provideFileWriter((Event) item, STOP), item);
                            return;
                        }
                        if (item instanceof OtherEvent) {
                            csvMapper.writer(otherEventSchema).writeValue(fileWriterProvider.provideFileWriter((Event) item, OTHER), item);
                            return;
                        }
                        if (item instanceof UnsignedEvent) {
                            csvMapper.writer(unsignedEventSchema).writeValue(fileWriterProvider.provideFileWriter((Event) item, UNSIGNED), item);
                            return;
                        }
                        if (item instanceof LightPriorityEvent) {
                            csvMapper.writer(lightPriorityEventSchema).writeValue(fileWriterProvider.provideFileWriter((Event) item, LIGHT), item);
                            return;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }
}
