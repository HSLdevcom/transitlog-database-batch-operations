package fi.hsl.common.batch.filewriters;


import fi.hsl.domain.TableType;
import fi.hsl.domain.VehiclePosition;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class CachedWriterFactoryTest {

    @Test
    public void createFileWriterProvider_twoconsecutive_hours_shouldCreateTwoDifferentFiles() throws IOException {
        WriterProvider fileWriterProvider = CachedWriterFactory.createFileWriterProvider();

        VehiclePosition firstHourInstance = new VehiclePosition();
        firstHourInstance.setTableType(TableType.VEHICLEPOSITION);
        firstHourInstance.setTst(CalendarUtil.timeStampFor(1, 1, 1));


        VehiclePosition secondHourInstance = new VehiclePosition();
        secondHourInstance.setTst(CalendarUtil.timeStampFor(1, 1, 2));
        secondHourInstance.setTableType(TableType.VEHICLEPOSITION);

        WriterProvider.Writer firstWriter = fileWriterProvider.buildFileWriter(firstHourInstance);
        WriterProvider.Writer secondWriter = fileWriterProvider.buildFileWriter(secondHourInstance);


        assertNotEquals(firstWriter.getWriterTarget(), secondWriter.getWriterTarget());

    }

    @Test
    public void createFileWriterProvider_twosamehour_shouldUseSameTarget() throws IOException {
        WriterProvider fileWriterProvider = CachedWriterFactory.createFileWriterProvider();

        VehiclePosition firstHourInstance = new VehiclePosition();
        firstHourInstance.setTableType(TableType.VEHICLEPOSITION);
        firstHourInstance.setTst(CalendarUtil.timeStampFor(1, 1, 1));


        VehiclePosition secondHourInstance = new VehiclePosition();
        secondHourInstance.setTst(CalendarUtil.timeStampFor(1, 1, 1));
        secondHourInstance.setTableType(TableType.VEHICLEPOSITION);

        WriterProvider.Writer firstWriter = fileWriterProvider.buildFileWriter(firstHourInstance);
        WriterProvider.Writer secondWriter = fileWriterProvider.buildFileWriter(secondHourInstance);


        assertEquals(firstWriter.getWriterTarget(), secondWriter.getWriterTarget());
    }
}