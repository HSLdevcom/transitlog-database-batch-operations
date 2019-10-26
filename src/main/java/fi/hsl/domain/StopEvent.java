package fi.hsl.domain;


public class StopEvent extends Event {
    public StopEvent(Vehicle item) {
        super(item, TableType.STOPEVENT);
    }

    public StopEvent() {
    }
}
