package fi.hsl.domain;


import javax.persistence.Entity;

@Entity
public class VehiclePosition extends Event {
    public VehiclePosition(Vehicle item) {
        super(item);
    }

    public VehiclePosition() {

    }

}
