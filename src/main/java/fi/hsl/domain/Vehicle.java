package fi.hsl.domain;

import lombok.Data;
import lombok.ToString;

import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

@Data
@ToString
public class Vehicle {
    private Long id;
    private Timestamp tst;
    private JourneyType journey_type;
    private EventType event_type;
    //Possible null value
    private String unique_vehicle_id;
    private Timestamp received_at;
    private String topic_prefix;
    private String topic_version;
    private Boolean is_ongoing;
    private TransportMode transport_mode;
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
    private LocationQualityMethod location_quality_method;
    private Integer stop;
    private String route;
    private Integer occu;

    public enum JourneyType {
        journey, deadrun, signoff
    }

    public enum EventType {
        VP, DUE, ARR, ARS, PDE, DEP, PAS, WAIT, TLR, TLA, DOO, DOC, DA, DOUT, BA, BOUT, VJA, VJOUT
    }

    public enum TransportMode {
        bus, train, tram, metro, ferry
    }

    public enum LocationQualityMethod {
        GPS, ODO, MAN, NA
    }
}
