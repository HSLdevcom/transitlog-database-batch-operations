package fi.hsl.common.batch;

import fi.hsl.domain.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.support.CompositeItemProcessor;

import java.util.Set;

import static fi.hsl.domain.Vehicle.EventType.*;

@Slf4j
public class DomainMappingProcessor extends CompositeItemProcessor<Vehicle, Event> {

    private final Set<Vehicle.EventType> stopEvents;
    private final Set<Vehicle.EventType> lightPriorityEvents;
    private final Set<Vehicle.EventType> otherEvents;

    public DomainMappingProcessor() {
        stopEvents = Set.of(new Vehicle.EventType[]{DUE, ARR, ARS, PDE, DEP, PAS, WAIT});
        lightPriorityEvents = Set.of(new Vehicle.EventType[]{TLR, TLA});
        otherEvents = Set.of(new Vehicle.EventType[]{DOO, DOC, DA, DOUT, BA, BOUT, VJA, VJOUT});
    }

    @Override
    public Event process(Vehicle item) {
        //Map to a new domain object here for persistence
        if (item.getEvent_type().equals(VP) && item.getJourney_type() == Vehicle.JourneyType.journey) {
            return new VehiclePosition(item);
        } else if (stopEvents.contains(item.getEvent_type())) {
            return new StopEvent(item);
        } else if (lightPriorityEvents.contains(item.getEvent_type())) {
            return new LightPriorityEvent(item);
        } else if (otherEvents.contains(item.getEvent_type())) {
            return new OtherEvent(item);
        } else if (item.getEvent_type().equals(VP) && (item.getJourney_type().equals(Vehicle.JourneyType.deadrun) || item.getJourney_type().equals(Vehicle.JourneyType.signoff)))
            return new UnsignedEvent(item);
        return null;
    }
}