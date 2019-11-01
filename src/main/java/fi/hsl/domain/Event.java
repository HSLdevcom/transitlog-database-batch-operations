package fi.hsl.domain;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

@MappedSuperclass
@EqualsAndHashCode
@JsonSubTypes({
        @JsonSubTypes.Type(value = VehiclePosition.class, name = "vehicleposition"),
        @JsonSubTypes.Type(value = LightPriorityEvent.class, name = "lightpriorityevent"),
        @JsonSubTypes.Type(value = OtherEvent.class, name = "otherevent"),
        @JsonSubTypes.Type(value = StopEvent.class, name = "stopevent"),
        @JsonSubTypes.Type(value = UnsignedEvent.class, name = "unsignedevent")
})
@Data
public abstract class Event {
    private Timestamp tst;
    private String unique_vehicle_id;
    @Enumerated(EnumType.STRING)
    private Vehicle.EventType event_type;
    @Enumerated(EnumType.STRING)
    private Vehicle.JourneyType journey_type;
    @Id
    private UUID uuid;
    private Timestamp received_at;
    private String topic_prefix;
    private String topic_version;
    private Boolean is_ongoing;
    @Enumerated(value = EnumType.STRING)
    private Vehicle.TransportMode mode;
    private Integer owner_operator_id;
    private Integer vehicle_number;
    private String route_id;
    private Integer direction_id;
    private String headsign;
    private Time journey_start_time;
    private String next_stop_id;
    private Integer geohash_level;
    private Double topic_latitude;
    private Double topic_longitude;
    private Double lat;
    @Column(name = "long")
    private Double longitude;
    private String desi;
    private Integer dir;
    private Integer oper;
    private Integer veh;
    private BigInteger tsi;
    private Double spd;
    private Integer hdg;
    private Double acc;
    private Integer dl;
    private Double odo;
    private Boolean drst;
    private Date oday;
    private Integer jrn;
    private Integer line;
    private Time start;
    @Column(name = "loc")
    @Enumerated(value = EnumType.STRING)
    private Vehicle.LocationQualityMethod location_quality_method;
    private Integer stop;
    private String route;
    private Integer occu;

    public Event(Vehicle item) {
        this.tst = item.getTst();
        this.unique_vehicle_id = item.getUnique_vehicle_id();
        this.event_type = item.getEvent_type();
        this.journey_type = item.getJourney_type();
        this.uuid = UUID.randomUUID();
        this.received_at = item.getReceived_at();
        this.topic_prefix = item.getTopic_prefix();
        this.topic_version = item.getTopic_version();
        this.is_ongoing = item.getIs_ongoing();
        this.mode = item.getTransport_mode();
        this.owner_operator_id = item.getOwner_operator_id();
        this.vehicle_number = item.getVehicle_number();
        this.route_id = item.getRoute_id();
        this.direction_id = item.getDirection_id();
        this.headsign = item.getHeadsign();
        this.journey_start_time = item.getJourney_start_time();
        this.next_stop_id = item.getNext_stop_id();
        this.geohash_level = item.getGeohash_level();
        this.topic_latitude = item.getTopic_latitude();
        this.topic_longitude = item.getTopic_longitude();
        this.desi = item.getDesi();
        this.dir = item.getDir();
        this.oper = item.getOper();
        this.veh = item.getVeh();
        this.tsi = item.getTsi();
        this.spd = item.getSpd();
        this.hdg = item.getHdg();
        this.lat = item.getLat();
        this.longitude = item.getLongitude();
        this.acc = item.getAcc();
        this.dl = item.getDl();
        this.odo = item.getOdo();
        this.drst = item.getDrst();
        this.oday = item.getOday();
        this.jrn = item.getJrn();
        this.line = item.getLine();
        this.start = item.getStart();
        this.location_quality_method = item.getLocation_quality_method();
        this.stop = item.getStop();
        this.route = item.getRoute();
        this.occu = item.getOccu();
    }

    public Event() {
    }


}
