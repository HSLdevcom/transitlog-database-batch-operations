package fi.hsl.domain;

public class UnsignedEvent extends Event {
    public UnsignedEvent(Vehicle item) {
        super(item, TableType.UNSIGNED);
    }

    public UnsignedEvent() {
    }
}
