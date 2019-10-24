package fi.hsl.domain;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class EventId implements Serializable {
    private Timestamp tst;
    private String unique_vehicle_id;
    @Enumerated(EnumType.STRING)
    private Vehicle.EventType event_type;
    @Enumerated(EnumType.STRING)
    private Vehicle.JourneyType journey_type;
    private UUID uuid;

    public EventId() {
    }

    EventId(Timestamp tst, String unique_vehicle_id, Vehicle.EventType event_type, Vehicle.JourneyType journey_type, UUID uuid) {
        this.tst = tst;
        this.unique_vehicle_id = unique_vehicle_id;
        this.event_type = event_type;
        this.journey_type = journey_type;
        this.uuid = uuid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventId eventId = (EventId) o;
        return Objects.equals(tst, eventId.tst) &&
                Objects.equals(unique_vehicle_id, eventId.unique_vehicle_id) &&
                event_type == eventId.event_type &&
                journey_type == eventId.journey_type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(tst, unique_vehicle_id, event_type, journey_type);
    }
}