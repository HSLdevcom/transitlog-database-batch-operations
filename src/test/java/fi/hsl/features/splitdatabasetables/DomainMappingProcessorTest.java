package fi.hsl.features.splitdatabasetables;

import fi.hsl.common.batch.DomainMappingProcessor;
import fi.hsl.domain.*;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;
import java.util.stream.Collectors;

import static fi.hsl.domain.Vehicle.EventType.*;
import static fi.hsl.domain.Vehicle.JourneyType.deadrun;
import static fi.hsl.domain.Vehicle.JourneyType.signoff;
import static org.junit.Assert.assertTrue;


public class DomainMappingProcessorTest {
    private DomainMappingProcessor domainMappingProcessor;

    @Before
    public void init() {
        this.domainMappingProcessor = new DomainMappingProcessor();
    }

    @Test
    public void process_vehiclepositiontype_shouldReturnVehiclePositionEntity() {
        Vehicle vehicle = new Vehicle();
        vehicle.setJourney_type(Vehicle.JourneyType.journey);
        vehicle.setEvent_type(Vehicle.EventType.VP);
        Object process = domainMappingProcessor.process(vehicle);
        assertTrue(process instanceof VehiclePosition);
    }


    @Test
    public void process_unsignedevent_shouldReturnUnsignedEventEntity() {
        Set<Vehicle.JourneyType> unsignedJourneyTypes = Set.of(new Vehicle.JourneyType[]{
                deadrun, signoff
        });
        unsignedJourneyTypes.stream()
                .map(journeyType -> {
                    Vehicle vehicle = new Vehicle();
                    vehicle.setEvent_type(VP);
                    vehicle.setJourney_type(journeyType);
                    return vehicle;
                })
                .collect(Collectors.toList())
                .forEach(vehicle -> assertTrue(domainMappingProcessor.process(vehicle) instanceof UnsignedEvent));
    }

    @Test
    public void process_othereventtype_shouldReturnOtherEventEntity() {
        Set<Vehicle.EventType> otherEvents = Set.of(new Vehicle.EventType[]{DOO, DOC, DA, DOUT, BA, BOUT, VJA, VJOUT});
        otherEvents.stream()
                .map(this::createVehicle)
                .collect(Collectors.toList())
                .forEach(vehicle -> assertTrue(domainMappingProcessor.process(vehicle) instanceof OtherEvent));
    }

    private Vehicle createVehicle(Vehicle.EventType eventType) {
        Vehicle vehicle = new Vehicle();
        vehicle.setEvent_type(eventType);
        return vehicle;
    }

    @Test
    public void process_stopeventtype_shouldReturnStopEventEntity() {
        Set<Vehicle.EventType> stopEvents = Set.of(new Vehicle.EventType[]{DUE, ARR, ARS, PDE, DEP, PAS, WAIT});
        stopEvents.stream()
                .map(this::createVehicle)
                .collect(Collectors.toList())
                .forEach(vehicle -> assertTrue(domainMappingProcessor.process(vehicle) instanceof StopEvent));
    }

    @Test
    public void process_lightprioriyeventtype_shouldReturnLightPriorityEventEntity() {
        Set<Vehicle.EventType> lightPriorityEvents = Set.of(new Vehicle.EventType[]{TLR, TLA});
        lightPriorityEvents.stream().map(this::createVehicle)
                .collect(Collectors.toList())
                .forEach(vehicle -> assertTrue(domainMappingProcessor.process(vehicle) instanceof LightPriorityEvent));
    }
}
