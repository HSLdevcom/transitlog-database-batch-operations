package fi.hsl.domain;

public class OtherEvent extends Event {
    public OtherEvent(Vehicle item) {
        super(item, TableType.OTHEREVENT);
    }

    public OtherEvent() {

    }
}
