package fi.hsl.domain;


public class LightPriorityEvent extends Event {
    public LightPriorityEvent(Vehicle item) {
        super(item, TableType.LIGHTPRIORITYEVENT);
    }

    public LightPriorityEvent() {
    }
}
