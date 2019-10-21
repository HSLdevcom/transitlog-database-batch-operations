package fi.hsl.domain;

import lombok.EqualsAndHashCode;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;
import java.sql.Timestamp;

@Embeddable
@EqualsAndHashCode
public class EventId implements Serializable {
    private Timestamp tst;
    private String unique_vehicle_id;
    @Enumerated(EnumType.STRING)
    private Vehicle.EventType event_type;
    @Enumerated(EnumType.STRING)
    private Vehicle.JourneyType journey_type;

    public EventId() {
    }

    public EventId(Timestamp tst, String unique_vehicle_id, Vehicle.EventType event_type, Vehicle.JourneyType journey_type) {
        this.tst = tst;
        this.unique_vehicle_id = unique_vehicle_id;
        this.event_type = event_type;
        this.journey_type = journey_type;
    }
}