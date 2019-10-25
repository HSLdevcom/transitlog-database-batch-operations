package fi.hsl.domain;


public class VehiclePosition extends Event {
    public VehiclePosition(Vehicle item) {
        super(item, TableType.VEHICLEPOSITION);
    }

    public VehiclePosition() {

    }

}
