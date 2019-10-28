package fi.hsl.common.batch.filewriters;

import fi.hsl.domain.Event;
import fi.hsl.domain.OtherEvent;
import fi.hsl.domain.TableType;
import fi.hsl.domain.VehiclePosition;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Calendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class FileWriterTest {
    private FileWriter fileWriter;

    @Before
    public void init() {
        this.fileWriter = new FileWriter();
    }

    @Test
    public void buildFileWriterContext_sameDatesTwoDifferentEvents_shouldReturnIndividualContexts() throws IOException {
        FileWriter.WriterContext vp_Context = fileWriter.buildFileWriterContext(createVehiclePositionEvent(1, 1, 1));

        FileWriter.WriterContext oe_context = fileWriter.buildFileWriterContext(createOtherEvent(1, 1, 1));
        assertNotEquals(vp_Context.getGeneratedAbsoluteFileName(), oe_context.getGeneratedAbsoluteFileName());
    }

    private Event createOtherEvent(int months, int days, int hours) {
        OtherEvent otherEvent = new OtherEvent();
        otherEvent.setTst(getTimestamp(months, days, hours));
        otherEvent.setTableType(TableType.OTHEREVENT);
        return otherEvent;
    }

    private Event createVehiclePositionEvent(int months, int days, int hours) {
        VehiclePosition vehiclePosition = new VehiclePosition();
        vehiclePosition.setTst(getTimestamp(months, days, hours));
        vehiclePosition.setTableType(TableType.VEHICLEPOSITION);
        return vehiclePosition;
    }

    private Timestamp getTimestamp(int months, int days, int hours) {
        Calendar instance = Calendar.getInstance();
        instance.set(Calendar.MONTH, months);
        instance.set(Calendar.HOUR_OF_DAY, hours);
        instance.set(Calendar.DAY_OF_MONTH, days);
        return new Timestamp(instance.getTimeInMillis());
    }

    @Test
    public void buildFileWriterContext_sameDatesTwoExecutes_closeFileWriterInBetween_shouldReturnNewContext() throws IOException {
        FileWriter.WriterContext context = fileWriter.buildFileWriterContext(createVehiclePositionEvent(1, 1, 1));
        context.getFileWriter().close();

        //Try to flush
        FileWriter.WriterContext notReusedContext = fileWriter.buildFileWriterContext(createVehiclePositionEvent(1, 1, 1));
        assertNotEquals(context.getFileWriter(), notReusedContext.getFileWriter());
    }

    @Test
    public void buildFileWriterContext_sameDatesTwoExecutes_shouldReturnReusedContext() throws IOException {
        FileWriter.WriterContext context = fileWriter.buildFileWriterContext(createVehiclePositionEvent(1, 1, 1));
        FileWriter.WriterContext reusedContext = fileWriter.buildFileWriterContext(createVehiclePositionEvent(1, 1, 1));
        assertEquals(context.getFileWriter(), reusedContext.getFileWriter());

    }

    @Test
    public void buildFileWriterContext_date_1_1_1_VP_shouldReturnProperFIlePath() throws IOException {
        FileWriter.WriterContext writerContext = fileWriter.buildFileWriterContext(createVehiclePositionEvent(1, 1, 1));
        String date_1_1_1 = writerContext.getGeneratedAbsoluteFileName();
        assertEquals(date_1_1_1, "/csv/VEHICLEPOSITION/VEHICLEPOSITION_1_1_1.csv");
    }
}