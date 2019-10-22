package fi.hsl.domain;


import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

@MappedSuperclass
abstract class Event implements Persistable<EventId> {
    @Embedded
    @Id
    private EventId event;
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
        this.event = new EventId(item.getTst(), item.getUnique_vehicle_id(), item.getEvent_type(), item.getJourney_type());
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

    @Override
    public boolean equals(Object obj) {
        return false;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public EventId getId() {
        return event;
    }

    @Override
    public boolean isNew() {
        //Force JPA to ASSUME ENTITY IS NEW to delegate conflict checking to database
        return true;
    }


}
