package fi.hsl.features.splitdatabasetables;

import fi.hsl.domain.*;
import org.springframework.batch.item.support.CompositeItemProcessor;

import java.util.Set;

import static fi.hsl.domain.Vehicle.EventType.*;

public class DomainMappingProcessor extends CompositeItemProcessor<Vehicle, Object> {
    @Override
    public Object process(Vehicle item) {
        Set<Vehicle.EventType> stopEvents = Set.of(new Vehicle.EventType[]{DUE, ARR, ARS, PDE, DEP, PAS, WAIT});
        Set<Vehicle.EventType> lightPriorityEvents = Set.of(new Vehicle.EventType[]{TLR, TLA});
        Set<Vehicle.EventType> otherEvents = Set.of(new Vehicle.EventType[]{DOO, DOC, DA, DOUT, BA, BOUT, VJA, VJOUT});
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