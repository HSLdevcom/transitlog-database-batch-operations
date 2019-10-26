package fi.hsl.common.batch;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import fi.hsl.common.batch.filewriters.WriterProvider;
import fi.hsl.domain.*;
import org.springframework.batch.item.ItemWriter;

import java.io.IOException;
import java.util.List;


class CSVItemWriter implements ItemWriter<Event> {

    private final CsvMapper csvMapper;
    private final CsvSchema vehiclePositionSchema;
    private final CsvSchema stopEventSchema;
    private final CsvSchema otherEventSchema;
    private final CsvSchema unsignedEventSchema;
    private final CsvSchema lightPriorityEventSchema;
    private final WriterProvider fileWriterProvider;

    CSVItemWriter(WriterProvider writerProvider) throws IOException {

        this.fileWriterProvider = writerProvider;
        csvMapper = new CsvMapper();
        //Do not autoclose filewriter from the csv mapper to avoid performance penalty, filewriter stream closing is handled by the provider
        csvMapper.disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);

        //Create schmenas first for the csvMapper
        lightPriorityEventSchema = csvMapper.schemaFor(LightPriorityEvent.class);
        unsignedEventSchema = csvMapper.schemaFor(UnsignedEvent.class);
        otherEventSchema = csvMapper.schemaFor(OtherEvent.class);
        stopEventSchema = csvMapper.schemaFor(StopEvent.class);
        vehiclePositionSchema = csvMapper.schemaFor(VehiclePosition.class);

    }

    //Sequential write to disk is faster
    @Override
    public synchronized void write(List<? extends Event> items) throws Exception {
        items
                .forEach(item -> {
                    try {
                        if (item.getTableType().equals(TableType.VEHICLEPOSITION)) {
                            csvMapper.writer(vehiclePositionSchema).writeValue(fileWriterProvider.buildFileWriter(item).getWriterOnFile(), item);
                            return;
                        }
                        if (item.getTableType().equals(TableType.STOPEVENT)) {
                            csvMapper.writer(stopEventSchema).writeValue(fileWriterProvider.buildFileWriter(item).getWriterOnFile(), item);
                            return;
                        }
                        if (item.getTableType().equals(TableType.OTHEREVENT)) {
                            csvMapper.writer(otherEventSchema).writeValue(fileWriterProvider.buildFileWriter(item).getWriterOnFile(), item);
                            return;
                        }
                        if (item.getTableType().equals(TableType.UNSIGNED)) {
                            csvMapper.writer(unsignedEventSchema).writeValue(fileWriterProvider.buildFileWriter(item).getWriterOnFile(), item);
                            return;
                        }
                        if (item.getTableType().equals(TableType.LIGHTPRIORITYEVENT)) {
                            csvMapper.writer(lightPriorityEventSchema).writeValue(fileWriterProvider.buildFileWriter(item).getWriterOnFile(), item);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }
}
